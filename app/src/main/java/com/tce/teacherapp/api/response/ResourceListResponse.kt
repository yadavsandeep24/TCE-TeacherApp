package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResourceListResponse (
    @Expose
    val id: String,

    @Expose
    @SerializedName("node")
    val resourceList: List<ResourceResponse>
)