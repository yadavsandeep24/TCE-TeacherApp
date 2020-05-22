package com.tce.teacherapp.repository

import android.content.SharedPreferences
import android.util.Log
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.api.response.BookResponse
import com.tce.teacherapp.api.response.GradeResponse
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.db.entity.Book
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.Node
import com.tce.teacherapp.db.entity.Subject
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.CacheResponseHandler
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.PreferenceKeys
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FlowPreview
class MainRepositoryImpl
@Inject
constructor(
    val subjectDao: SubjectsDao,
    val tceService: TCEService,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) : MainRepository {
    private val TAG: String = "AppDebug"
    override fun getGrades(stateEvent: StateEvent): Flow<DataState<SubjectViewState>> {
        return object : NetworkBoundResource<List<GradeResponse>, List<Grade>, SubjectViewState>(
            dispatcher = IO,
            stateEvent = stateEvent,
            apiCall = {
                tceService.getGrades()
            },
            cacheCall = {
                subjectDao.getGradeListData()
            }
        ) {
            override suspend fun updateCache(networkObject: List<GradeResponse>) {
                val gradeList = toGradeList(networkObject)
                withContext(IO) {
                    for (grade in gradeList) {
                        try {
                            // Launch each insert as a separate job to be executed in parallel
                            launch {
                                Log.d(TAG, "updateLocalDb: inserting grade: $grade")
                                subjectDao.insertGrade(grade)
                                for (subject in grade.subjectList) {
                                    try {
                                        launch {
                                            subjectDao.insert(subject)
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            TAG,
                                            "updateLocalDb: error updating cache data on subject  with title: ${subject.title}. " +
                                                    "${e.message}"
                                        )
                                        // Could send an error report here or something but I don't think you should throw an error to the UI
                                        // Since there could be many blog posts being inserted/updated.
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "updateLocalDb: error updating cache data on blog post with gradeTitle: ${grade.gradeTitle}. " +
                                        "${e.message}"
                            )
                            // Could send an error report here or something but I don't think you should throw an error to the UI
                            // Since there could be many blog posts being inserted/updated.
                        }
                    }
                }
            }

            override fun handleCacheSuccess(
                isJobComplete: Boolean,
                resultObj: List<Grade>
            ): DataState<SubjectViewState> {
                if (resultObj.isNotEmpty()) {
                    sharedPrefsEditor.putString(
                        PreferenceKeys.APP_USER_SELECTED_GRADE_ID,
                        resultObj[0].id
                    ).commit()
                    sharedPrefsEditor.putInt(PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION, 0)
                        .commit()
                }

                val viewState = SubjectViewState(
                    gradeList = resultObj
                )
                return DataState.data(
                    response = null,
                    data = viewState,
                    stateEvent = stateEvent
                )
            }

        }.result

    }

    override fun getSubjects(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {
        val apiResult = safeCacheCall(IO) {
            val selectedGrade =
                sharedPreferences.getString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, "")
            selectedGrade?.let {
                if (query.isEmpty()) {
                    subjectDao.getSubjectListData(it)
                } else {
                    subjectDao.getSubjectListData(query, it)
                }
            }
        }
        emit(
            object : CacheResponseHandler<SubjectViewState, List<Subject>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<Subject>): DataState<SubjectViewState> {
                    return DataState.data(
                        data = SubjectViewState(
                            subjectList = resultObj,
                            selectedGradePosition = sharedPreferences.getInt(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION,
                                0
                            )
                        ),
                        response = null,
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )

    }

    override fun setSelectedGrade(
        grade: Grade,
        position: Int,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {
        sharedPrefsEditor.putString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, grade.id).commit()
        sharedPrefsEditor.putInt(PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION, position).commit()
        val apiResult = safeCacheCall(IO) {
            val selectedGrade =
                sharedPreferences.getString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, "")
            selectedGrade?.let {
                subjectDao.getSubjectListData(it)
            }
        }
        emit(
            object : CacheResponseHandler<SubjectViewState, List<Subject>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<Subject>): DataState<SubjectViewState> {
                    return DataState.data(
                        data = SubjectViewState(
                            subjectList = resultObj,
                            selectedGradePosition = sharedPreferences.getInt(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION,
                                0
                            )
                        ),
                        response = null,
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }

    override fun getTopicList(
        query: String,
        bookID: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        return object : NetworkBoundResource<List<BookResponse>, List<Node>, SubjectViewState>(
            dispatcher = IO,
            stateEvent = stateEvent,
            apiCall = {
                tceService.getBooks()
            },
            cacheCall = {
                if (query.isEmpty()) {
                    subjectDao.getTopicListData(bookID)
                } else {
                    subjectDao.getTopicListData(query, bookID)
                }
            }
        ) {
            override suspend fun updateCache(networkObject: List<BookResponse>) {
                val bookList = toBookList(networkObject)
                withContext(IO) {
                    for (book in bookList) {
                        try {
                            // Launch each insert as a separate job to be executed in parallel
                            launch {
                                Log.d(TAG, "updateLocalDb: inserting book: $book")
                                subjectDao.insertBook(book)
                                for (node in book.node) {
                                    try {
                                            subjectDao.insertNode(node)
                                            for (nodex in node.node) {
                                                try {
                                                        subjectDao.inserNodeX(nodex)
                                                        for (nodexx in nodex.node) {
                                                            try {
                                                                    subjectDao.insertNodeXX(nodexx)
                                                                    for (nodexxx in nodexx.node) {
                                                                        try {
                                                                                subjectDao.insertNodeXXX(nodexxx)
                                                                        } catch (e: Exception) {
                                                                        }
                                                                    }
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun handleCacheSuccess(
                isjobComplete: Boolean,
                resultObj: List<Node>
            ): DataState<SubjectViewState> {
                val viewState = SubjectViewState(
                    topicList = resultObj
                )
                return if(isjobComplete){
                    DataState.data(
                        response = null,
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }else{
                    DataState.data(
                        response = null,
                        data = null,
                        stateEvent = stateEvent
                    )
                }

            }

        }.result


    }

    fun toGradeList(grades: List<GradeResponse>): List<Grade> {
        val gradeList: ArrayList<Grade> = ArrayList()
        for (gradeResponse in grades) {
            val grade = gradeResponse.toGrade()
            gradeList.add(
                gradeResponse.toGrade()
            )
        }
        return gradeList
    }

    fun toBookList(books: List<BookResponse>): List<Book> {
        val bookList: ArrayList<Book> = ArrayList()
        for (bookResponse in books) {
            bookList.add(
                bookResponse.toBook()
            )
        }
        return bookList
    }

}





