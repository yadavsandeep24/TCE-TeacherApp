package com.tce.teacherapp.api

import com.tce.teacherapp.api.response.BookResponse
import com.tce.teacherapp.api.response.GradeResponse
import com.tce.teacherapp.api.response.LoginResponse
import com.tce.teacherapp.api.response.ResourceListResponse
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


}









