package com.tce.teacherapp.api

import com.tce.teacherapp.util.JsonUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.api.response.BookResponse
import com.tce.teacherapp.api.response.GradeResponse
import com.tce.teacherapp.api.response.LoginResponse
import com.tce.teacherapp.util.Constants
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeApiService
@Inject
constructor(
    private val jsonUtil: JsonUtil
): TCEService {

    var gradesJsonFileName: String = Constants.GRADES_DATA_FILENAME
    var networkDelay: Long = 0L
    override suspend fun login(email: String, password: String): LoginResponse {
        return LoginResponse("","","",0,"")
    }

    override suspend fun getGrades(): List<GradeResponse> {
        val rawJson = jsonUtil.readJSONFromAsset(gradesJsonFileName)
        val grades = Gson().fromJson<List<GradeResponse>>(
            rawJson,
            object : TypeToken<List<GradeResponse>>() {}.type
        )
        delay(networkDelay)
        return grades
    }

    override suspend fun getBooks(): List<BookResponse> {
        val rawJson = jsonUtil.readJSONFromAsset(gradesJsonFileName)
        val books = Gson().fromJson<List<BookResponse>>(
            rawJson,
            object : TypeToken<List<GradeResponse>>() {}.type
        )
        delay(networkDelay)
        return books
    }

    /*   override suspend fun getBlogPosts(category: String): List<BlogPost> {
           val rawJson = jsonUtil.readJSONFromAsset(blogPostsJsonFileName)
           val blogs = Gson().fromJson<List<BlogPost>>(
               rawJson,
               object : TypeToken<List<BlogPost>>() {}.type
           )
           val filteredBlogs = blogs.filter { blogPost -> blogPost.category.equals(category) }
           delay(networkDelay)
           return filteredBlogs
       }

       override suspend fun getAllBlogPosts(): List<BlogPost> {
           val rawJson = jsonUtil.readJSONFromAsset(blogPostsJsonFileName)
           val blogs = Gson().fromJson<List<BlogPost>>(
               rawJson,
               object : TypeToken<List<BlogPost>>() {}.type
           )
           delay(networkDelay)
           return blogs
       }

       override suspend fun getCategories(): List<Category> {
           val rawJson = jsonUtil.readJSONFromAsset(categoriesJsonFileName)
           val categories = Gson().fromJson<List<Category>>(
               rawJson,
               object : TypeToken<List<Category>>() {}.type
           )
           delay(networkDelay)
           return categories
       }*/

}