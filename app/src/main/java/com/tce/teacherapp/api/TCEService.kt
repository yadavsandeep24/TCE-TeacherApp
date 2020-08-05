package com.tce.teacherapp.api

import com.tce.teacherapp.api.response.*
import com.tce.teacherapp.db.entity.DailyPlanner
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface TCEService {
    companion object {
        const val BASE_URL = "http://tceapi.zeptolearn.com:3000/"
    }

    @POST("account/login")
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("grades")
    suspend fun getGrades(): List<GradeResponse>

    @GET("Books")
    suspend fun getBooks(): List<BookResponse>

    @GET("Resources")
    suspend fun getTopicResources(): List<ResourceListResponse>

    @GET("LessonPlan")
    suspend fun getLessonPlan(): List<DailyPlanner>

    @GET("Students")
    suspend fun getStudentList(): List<StudentListResponseItem>

    @GET("AttendanceData")
    suspend fun getStudentAttendanceList(): List<StudentAttendanceResponseItem>

    @GET("FeedbackMaster")
    suspend fun getFeedbackMaster(): List<FeedbackMasterDataItem>

    @GET("StudentPortfolio")
    suspend fun getStudentPortFolioData(): List<StudentPortFolioResponseItem>

    @GET("GalleryData")
    suspend fun getStudentGalleryData(): List<StudentGalleryResponseItem>


}









