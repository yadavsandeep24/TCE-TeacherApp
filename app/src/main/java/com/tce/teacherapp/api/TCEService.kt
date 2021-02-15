package com.tce.teacherapp.api

import com.tce.teacherapp.api.response.*
import com.tce.teacherapp.api.response.LoginResponse
import com.tce.teacherapp.api.response.tceapi.*
import com.tce.teacherapp.db.entity.DailyPlanner
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface TCEService {
    companion object {
        const val BASE_URL_ZL = "http://tceapi.zeptolearn.com:3000/"
        const val BASE_URL = "http://172.18.1.57:8080/"
    }


    @GET("tce-auth-api/0/api/1/sso/clientid")
    suspend fun getClientId(): ClientIDResponse


    @GET("tce-auth-api/0/api/{api-version}/admin/organizations/{searchTerm}")
    suspend fun getSchoolLists(
        @Path("api-version", encoded = true) apiVersion: String,
        @Path("searchTerm", encoded = true) searchTerm: String
    ): SchoolListResponse

    @Multipart
    @POST("tce-auth-api/0/api/{api-version}/sso/token")
    suspend fun getAccessToken(
        @Path("api-version", encoded = true) apiVersion: String,
        @Part("username") request: RequestBody,
        @Part("password") passwordB: RequestBody,
        @Part("grant_type") grantTypeB: RequestBody
    ): com.tce.teacherapp.api.response.tceapi.LoginResponse


    @FormUrlEncoded
    @POST("tce-auth-api/1/api/{api-version}/sso/extend")
     fun extendToken(
        @Path("api-version", encoded = true) apiVersion: String,
        @Field("access_token") accessToken :String
    ): Call<ExtendTokenResponse>

    @Multipart
    @POST("tce-auth-api/0/api/{api-version}/sso/token")
     fun getRefreshToken(
        @Path("api-version", encoded = true) apiVersion: String,
        @Part("refresh_token") refreshToken: RequestBody,
        @Part("access_token") accessToken: RequestBody,
        @Part("grant_type") grantType: RequestBody
    ): Call<com.tce.teacherapp.api.response.tceapi.RefreshTokenResponse>

    @GET("tce-teach-api/1/api/{api-version}/curriculum")
    suspend fun getCurriculum(
        @Header("Authorization") authorisation :String,
        @Path("api-version", encoded = true) apiVersion: String,
        @Query("mode") mode: String
    ): CurriculumResponse


    @GET("tce-teach-api/1/api/{api-version}/serve/tp")
    suspend fun getTCETopic(
        @Header("Authorization") authorisation :String,
        @Path("api-version", encoded = true) apiVersion: String,
        @Query("gradeId") gradeID: String,
        @Query("subjectId") subjeciD: String,
        @Query("ids") topicId: String
    ):List<TCETopicResponseItem>

    @GET("tce-teach-api/1/api/{api-version}/curriculum/book/{bookID}")
    suspend fun getTCEBook(
        @Header("Authorization") authorisation :String,
        @Path("api-version", encoded = true) apiVersion: String,
        @Path("bookID", encoded = true) bookId: String
    ):TCEBookResponse


    @Streaming
    @GET("tce-repo-api/1/web/{api-version}/content/fileservice/{topicid}/{assetID}/{filename}")
    suspend fun getFileUrl(
        @Header("Cookie") token:String,
        @Path("api-version", encoded = true) apiVersion: String,
        @Path("topicid", encoded = true) topicID: String,
        @Path("aassetID", encoded = true) assetID: String,
        @Path("filename", encoded = true) fileNAME: String
    ):ResponseBody



    @POST("account/login")
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String
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

    @GET("Message")
    suspend fun getMessageList(): List<MessageListResponseItem>

    @GET("ContentType")
    suspend fun getContentType(): List<ContentType>


}









