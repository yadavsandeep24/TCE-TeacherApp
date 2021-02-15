package com.tce.teacherapp.api.response.tceapi

import com.google.gson.annotations.Expose

data class LoginResponse(

    @Expose
    val access_token: String,

    @Expose
    val expires_in: Int,

    @Expose
    val loginstatus: Int,

    @Expose
    val mode: String,

    @Expose
    val refresh_token: String,

    @Expose
    val scope: String,

    @Expose
    val token_type: String,

    @Expose
    val tstamp: Long
)