package com.tce.teacherapp.api.response.tceapi

import com.google.gson.annotations.Expose

data class CurriculumResponse(
    @Expose
    val curriculumVO: CurriculumVO,

    @Expose
    val sessionKeys: String
)