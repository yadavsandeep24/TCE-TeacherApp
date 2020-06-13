package com.tce.teacherapp.repository

import android.app.Application
import android.content.SharedPreferences
import android.os.ParcelFileDescriptor.open
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.api.response.BookResponse
import com.tce.teacherapp.api.response.GradeResponse
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.db.entity.*
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.channels.DatagramChannel.open
import javax.inject.Inject

@FlowPreview
class MainRepositoryImpl
@Inject
constructor(
    val subjectDao: SubjectsDao,
    val tceService: TCEService,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor,
    val application: Application
) : MainRepository {
    private val TAG: String = "AppDebug"
    override fun getGrades(stateEvent: StateEvent): Flow<DataState<SubjectViewState>> = flow{

        val isNewSession = sharedPreferences.getBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES,true)
         if(isNewSession) {
             val apiResult = safeApiCall(IO){
                 tceService.getGrades()
             }
             emit(
                 object: ApiResponseHandler<SubjectViewState, List<GradeResponse>>(
                     response = apiResult,
                     stateEvent = stateEvent
                 ){
                     override suspend fun handleSuccess(resultObj: List<GradeResponse>): DataState<SubjectViewState> {
                         val gradeList = toGradeList(resultObj)
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
                         if (resultObj.isNotEmpty()) {
                             sharedPrefsEditor.putString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, resultObj[0].id).commit()
                             sharedPrefsEditor.putInt(PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION, 0).commit()
                             sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES, false).commit()
                         }
                         val viewState = SubjectViewState(gradeList = gradeList)
                         return DataState.data(
                             response = null,
                             data = viewState,
                             stateEvent = stateEvent
                         )

                     }
                 }.getResult()
             )
         }else{
             val apiResult = safeCacheCall(IO) {
                 val selectedGrade =
                     sharedPreferences.getString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, "")
                 selectedGrade?.let {
                         subjectDao.getGradeListData()
                 }
             }
             emit(
                 object : CacheResponseHandler<SubjectViewState, List<Grade>>(
                     response = apiResult,
                     stateEvent = stateEvent
                 ) {
                     override suspend fun handleSuccess(resultObj: List<Grade>): DataState<SubjectViewState> {
                         if (resultObj.isNotEmpty()) {
                             sharedPrefsEditor.putString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, resultObj[0].id).commit()
                             sharedPrefsEditor.putInt(PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION, 0).commit()
                             sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES, false).commit()
                         }
                         return DataState.data(
                             data = SubjectViewState(gradeList = resultObj),
                             response = null,
                             stateEvent = stateEvent
                         )
                     }
                 }.getResult()
             )
         }
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
    ): Flow<DataState<SubjectViewState>>  =  flow{
        val isNewSession = sharedPreferences.getBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS,true)
        if(isNewSession){
            val apiResult = safeApiCall(IO){
                tceService.getBooks()
            }
            emit(
                object: ApiResponseHandler<SubjectViewState, List<BookResponse>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ){
                    override suspend fun handleSuccess(networkObject: List<BookResponse>): DataState<SubjectViewState> {
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
                        var topicList = subjectDao.getTopicListData(bookID)
                        if (query.isNotEmpty()) {
                            topicList = subjectDao.getTopicListData(query, bookID)
                        }
                        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS, false).commit()
                        val viewState = SubjectViewState(
                            topicList = topicList
                        )
                        return DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )
                    }


                }.getResult()
            )
        }else{
            val apiResult = safeCacheCall(IO) {
                if (query.isEmpty()) {
                    subjectDao.getTopicListData(bookID)
                } else {
                    subjectDao.getTopicListData(query, bookID)
                }
            }
            emit(
                object : CacheResponseHandler<SubjectViewState, List<Node>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<Node>): DataState<SubjectViewState> {
                        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS, false).commit()
                        val viewState = SubjectViewState(
                            topicList = resultObj
                        )
                        return DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun getMessage(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> = flow {
         val messageList = listOf(
            (com.tce.teacherapp.db.entity.Message(1,"School Announcement", "Hi teacher, please take note that school will be closed on the 20th feb. ",
                "ic_dummy_school.xml","1.49 pm","1",0,"10 January 2020", listOf(
                    MessageConversion(1,"Hi teacher, please take note that school will be closed on the 20th feb.","","","","","1.49 PM", "1", "Aishwarya"))
                )),
            (com.tce.teacherapp.db.entity.Message(2,"Class Apple", "Hi Parents, I am sharing what we have learnt in class.",
                "ic_dummy_class_apple.xml","1.49 pm","2", 13,"10 January 2020", listOf(
                    MessageConversion(2,"Hi Parents, I am sharing what we have learnt in class.","video","","","image","1.49 PM", "2", "Rajesh"))))
        )
        var selectedList : List<Message>;
        if(TextUtils.isEmpty(query)){
            selectedList = messageList
        }else{
            selectedList = messageList.filter { it.title.contains(query, ignoreCase = true) }
        }

        emit(
            DataState.data(
                data = MessageViewState(messageList = selectedList),
                stateEvent = stateEvent,
                response = null
            )
        )
    }

    override fun getStudentList( query: String,stateEvent: StateEvent): Flow<DataState<MessageViewState>> = flow {
        val studentList = listOf(
            Student(1, "Student Name 1", ""), Student(2, "Student Name 2", ""),
            Student(3, "Student Name 3", ""), Student(4, "Student Name 4", ""),
            Student(5, "Student Name 5", ""), Student(6, "Student Name 6", ""))
        var selectedList : List<Student>;
        if(TextUtils.isEmpty(query)){
            selectedList = studentList
        }else{
            selectedList = studentList.filter { it.name.contains(query,  ignoreCase = true) }
        }

        emit(
            DataState.data(
                data = MessageViewState(studentList = selectedList),
                stateEvent = stateEvent,
                response = null
            )
        )
    }

    override fun getResourceList( query: String,stateEvent: StateEvent): Flow<DataState<MessageViewState>> = flow {
        val resourceList = listOf(
            MessageResource(1, "", "","A Day in the Zoo - Modality",1, "TCE", false),
            MessageResource(2, "", "","Title Name - 1",2, "My Resource", false),
            MessageResource(3, "", "","Title Name - 2",3, "Shared", false))
        var selectedList : List<MessageResource>;
        if(TextUtils.isEmpty(query)){
            selectedList = resourceList
        }else{
            selectedList = resourceList.filter { it.title.contains(query, ignoreCase = true) }
        }

        emit(
            DataState.data(
                data = MessageViewState(resourceList = selectedList),
                stateEvent = stateEvent,
                response = null
            )
        )
    }

    override fun getSelectedResourceList(
        query: String,
        typeId: Int,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> = flow {
        val resourceList = listOf(
            MessageResource(1, "", "","A Day in the Zoo - Modality",1, "TCE", false),
            MessageResource(2, "", "","Title Name - 1",2, "My Resource", false),
            MessageResource(3, "", "","Title Name - 2",3, "Shared", false),
            MessageResource(4, "", "","Title Name - 3",3, "Shared", false))
        var selectedList : List<MessageResource>;
        if(TextUtils.isEmpty(query)){
            selectedList = resourceList.filter { it.typeId == typeId }
        }else{
            selectedList = resourceList.filter { it.title.contains(query, ignoreCase = true) && it.typeId == typeId}
        }

        emit(
            DataState.data(
                data = MessageViewState(selectedResourceList = selectedList),
                stateEvent = stateEvent,
                response = null
            )
        )
    }

    override fun getProfile(stateEvent: StateEvent): Flow<DataState<DashboardViewState>> = flow {
        //get data from json
        var jsonString: String  =""
        try {

            jsonString = application.assets.open("json/profile.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<Profile>() {}.type
            var persons: Profile = gson.fromJson(jsonString, listPersonType)
            emit(
                DataState.data(
                    data = DashboardViewState(profile = persons),
                    stateEvent = stateEvent,
                    response = null
                )
            )
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }




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





