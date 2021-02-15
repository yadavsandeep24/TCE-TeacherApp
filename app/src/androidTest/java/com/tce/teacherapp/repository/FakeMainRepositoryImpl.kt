package com.tce.teacherapp.repository

import android.app.Application
import com.tce.teacherapp.api.FakeApiService
import com.tce.teacherapp.api.response.GradeResponse
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.Profile
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
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
constructor() : MainRepository {

    private val CLASS_NAME: String = "FakeMainRepositoryImpl"

    lateinit var apiService: FakeApiService

    private fun throwExceptionIfApiServiceNotInitialzied() {
        if (!::apiService.isInitialized) {
            throw UninitializedPropertyAccessException(
                "Did you forget to set the ApiService in FakeMainRepositoryImpl?"
            )
        }
    }

    override fun getTCEApplication(): Application {
        TODO("Not yet implemented")
    }

    override fun getGrades(stateEvent: StateEvent): Flow<DataState<SubjectViewState>> {
        throwExceptionIfApiServiceNotInitialzied()
        return flow {

            val response = safeApiCall(Dispatchers.IO) { apiService.getGrades() }

            emit(
                object : ApiResponseHandler<SubjectViewState, List<GradeResponse>>(
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
        throwExceptionIfApiServiceNotInitialzied()
        return flow {

            val response = safeApiCall(Dispatchers.IO) { apiService.getGrades() }

            emit(
                object : ApiResponseHandler<SubjectViewState, List<GradeResponse>>(
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

    override fun setSelectedGrade(
        grade: Grade,
        position: Int,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        throwExceptionIfApiServiceNotInitialzied()
        return flow {

            val response = safeApiCall(Dispatchers.IO) { apiService.getGrades() }

            emit(
                object : ApiResponseHandler<SubjectViewState, List<GradeResponse>>(
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

    override fun getBookDetailList(
        query: String,
        bookID: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        throwExceptionIfApiServiceNotInitialzied()
        return flow {

            val response = safeApiCall(Dispatchers.IO) { apiService.getGrades() }

            emit(
                object : ApiResponseHandler<SubjectViewState, List<GradeResponse>>(
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

    override fun getMessage(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> {
        TODO("Not yet implemented")
    }

    override fun getMessageConversationList(
        type: String,
        messageId: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> {
        TODO("Not yet implemented")
    }

    override fun getStudentList(
        classId: Int,
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> {
        TODO("Not yet implemented")
    }

    override fun getResourceList(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> {
        TODO("Not yet implemented")
    }

    override fun getSelectedResourceList(
        query: String,
        type: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> {
        TODO("Not yet implemented")
    }


    override fun getProfile(stateEvent: StateEvent): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun updateProfilePic(
        resultUri: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun getTeacherDashboardData(
        id: Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun getParentDashboardData(
        id: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }


    override fun getEventList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun getTodayResourceList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun getLastViewedResourceList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun checkLoginMode(stateEvent: StateEvent): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun updatePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun updateProfile(
        userInfo: Profile,
        stateMessage: StateEvent
    ): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun getUserClassLists(stateEvent: StateEvent): Flow<DataState<DashboardViewState>> {
        TODO("Not yet implemented")
    }

    override fun getChapterList(
        query: String,
        topicId: String,
        bookId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        TODO("Not yet implemented")
    }

    override fun getTopicResourceList(
        query: String,
        topicId: String,
        chapterId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        TODO("Not yet implemented")
    }

    override fun getPlannerData(
        isShowLess: Boolean,
        stateEvent1: String?,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>> {
        TODO("Not yet implemented")
    }

    override fun getMonthlyPlannerData(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>> {
        TODO("Not yet implemented")
    }

    override fun setSelectedChildPosition(
        position: Int,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>> {
        TODO("Not yet implemented")
    }

    override fun getSelectedChildPosition(stateEvent: StateEvent): Flow<DataState<PlannerViewState>> {
        TODO("Not yet implemented")
    }

    override fun getChapterResourceType(
        chapterId: String,
        topicId: String,
        bookId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> {
        TODO("Not yet implemented")
    }

    override fun getStudentData(
        classId: Int,
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<StudentViewState>> {
        TODO("Not yet implemented")
    }

    override fun getStudentAttendanceData(stateEvent: StateEvent): Flow<DataState<StudentViewState>> {
        TODO("Not yet implemented")
    }

    override fun getFeedBackMasterData(stateEvent: StateEvent): Flow<DataState<StudentViewState>> {
        TODO("Not yet implemented")
    }

    override fun getGalleryData(
        type: Int,
        stateEvent: StateEvent
    ): Flow<DataState<StudentViewState>> {
        TODO("Not yet implemented")
    }

    override fun getStudentPortfolio(
        type: Int,
        stateEvent: StateEvent
    ): Flow<DataState<StudentViewState>> {
        TODO("Not yet implemented")
    }

    override fun getMessageResourceTypeList(stateEvent: StateEvent): Flow<DataState<MessageViewState>> {
        TODO("Not yet implemented")
    }

    override fun getCurriculumData(stateEvent: SubjectStateEvent): Flow<DataState<SubjectViewState>> {
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