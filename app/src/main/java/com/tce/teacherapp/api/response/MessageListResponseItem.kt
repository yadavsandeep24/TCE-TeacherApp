package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MessageListResponseItem(

    @Expose
    val FromId: String?,

    @Expose
    val Message: String?,

    @Expose
    val MsgId: String?,

    @Expose
    val Resource: List<Resource>?,

    @Expose
    val SendDate: String,

    @Expose
    val ToId: String?,

    @Expose
    val Type: String,

    @Expose
    val id: String?,

    @Expose
    val isAttachment: Boolean?,

    @Expose
    val isDelivered: Boolean?,

    @Expose
    val isRead: Boolean,

    @Expose
    val Title: String,

    var unReadCount: Int
) : Parcelable