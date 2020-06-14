package com.tce.teacherapp.db.entity

import com.google.gson.annotations.Expose

data class UserInfo(
    @Expose
    val profile: Profile
)