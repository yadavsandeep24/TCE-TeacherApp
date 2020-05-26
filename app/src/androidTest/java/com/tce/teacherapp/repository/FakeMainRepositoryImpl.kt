package com.tce.teacherapp.repository

import com.tce.teacherapp.api.FakeApiService
import com.tce.teacherapp.api.response.GradeResponse
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.ui.dashboard.subjects.SubjectsViewModel
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.ApiResponseHandler
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


/**
 * The only difference between this and the real MainRepositoryImpl is the ApiService is
 * fake and it's not being injected so I can change it at runtime.
 * That way I can alter the FakeApiService for each individual test.
 */
@Singleton
class FakeMainRepositoryImpl
@Inject
constructor(): MainRepository{

    private val CLASS_NAME: String = "FakeMainRepositoryImpl"

    lateinit var apiService: FakeApiService

    private fun throwExceptionIfApiServiceNotInitialzied(){
        if(!::apiService.isInitialized){
            throw UninitializedPropertyAccessException(
                "Did you forget to set the ApiService in FakeMainRepositoryImpl?"
            )
        }
    }

    override fun getGrades(stateEvent: StateEvent): Flow<DataState<SubjectViewState>> {
    throwExceptionIfApiServiceNotInitialzied()
        return flow{

            val response = safeApiCall(Dispatchers.IO){apiService.getGrades()}

            emit(
                object: ApiResponseHandler<SubjectViewState, List<GradeResponse>>(
                    response = response,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<GradeResponse>): DataState<SubjectViewState> {
                        val gradeList = toGradeList(resultObj)
                        val viewState = SubjectViewState(gradeList = gradeList)
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

    override fun getSubjects(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        TODO("Not yet implemented")
    }

    override fun setSelectedGrade(
        grade: Grade,
        position: Int,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        TODO("Not yet implemented")
    }

    override fun getTopicList(
        query: String,
        bookID: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        TODO("Not yet implemented")
    }

/*    @Throws(UninitializedPropertyAccessException::class)
    override fun getBlogs(stateEvent: StateEvent, category: String): Flow<DataState<SubjectViewState>> {
        throwExceptionIfApiServiceNotInitialzied()
        return flow{

            val response = safeApiCall(Dispatchers.IO){apiService.getGrades(category)}

            emit(
                object: ApiResponseHandler<MainViewState, List<BlogPost>>(
                    response = response,
                    stateEvent = stateEvent
                ) {
                    override fun handleSuccess(resultObj: List<BlogPost>): DataState<MainViewState> {
                        return DataState.data(
                            data = MainViewState(
                                listFragmentView = MainViewState.ListFragmentView(
                                    blogs = resultObj
                                )
                            ),
                            stateEvent = stateEvent
                        )
                    }

                }.result
            )
        }
    }

    @Throws(UninitializedPropertyAccessException::class)
    override fun getAllBlogs(stateEvent: StateEvent): Flow<DataState<MainViewState>> {
        throwExceptionIfApiServiceNotInitialzied()
        return flow{

            val response = safeApiCall(Dispatchers.IO){apiService.getAllBlogPosts()}

            emit(
                object: ApiResponseHandler<MainViewState, List<BlogPost>>(
                    response = response,
                    stateEvent = stateEvent
                ) {
                    override fun handleSuccess(resultObj: List<BlogPost>): DataState<MainViewState> {
                        return DataState.data(
                            data = MainViewState(
                                listFragmentView = MainViewState.ListFragmentView(
                                    blogs = resultObj
                                )
                            ),
                            stateEvent = stateEvent
                        )
                    }

                }.result
            )
        }
    }

    @Throws(UninitializedPropertyAccessException::class)
    override fun getCategories(stateEvent: StateEvent): Flow<DataState<MainViewState>> {
        throwExceptionIfApiServiceNotInitialzied()
        return flow{

            val response = safeApiCall(Dispatchers.IO){apiService.getCategories()}

            emit(
                object: ApiResponseHandler<MainViewState, List<Category>>(
                    response = response,
                    stateEvent = stateEvent
                ) {
                    override fun handleSuccess(resultObj: List<Category>): DataState<MainViewState> {
                        return DataState.data(
                            data = MainViewState(
                                listFragmentView = MainViewState.ListFragmentView(
                                    categories = resultObj
                                )
                            ),
                            stateEvent = stateEvent
                        )
                    }

                }.result
            )
        }
    }*/

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
}