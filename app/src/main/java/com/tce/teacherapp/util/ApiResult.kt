package com.tce.teacherapp.util

import com.google.gson.annotations.SerializedName

sealed class ApiResult<out T> {

    data class Success<out T>(val value: T) : ApiResult<T>()

    data class GenericError(

        @SerializedName("errorCode")
        val code: Int? = null,

        @SerializedName("errorMessage")
        val errorMessage: String? = null
    ) : ApiResult<Nothing>()

    object NetworkError : ApiResult<Nothing>()
}
