package com.tce.teacherapp.api.response.tceapi

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(

    @SerializedName("access_token")
    val access_token: String,

    @SerializedName("expires_in")
    val expires_in: Int,

    @SerializedName("loginstatus")
    val loginstatus: Int,

    @SerializedName("mode")
    val mode: String,

    @SerializedName("refresh_token")
    val refresh_token: String,

    @SerializedName("scope")
    val scope: String,

    @SerializedName("token_type")
    val token_type: String,

    @SerializedName("tstamp")
    val tstamp: Long
)