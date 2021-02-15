package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TCETopicResponseItem (

    @Expose
    val id: String,

    @Expose
    val playlistJson: String,

    @Expose
    val totalPageCount: Int,

    @Ignore
    var topicJsonResponse : TopicJsonResponse,

    @Ignore
    var accessToken :String
):Parcelable