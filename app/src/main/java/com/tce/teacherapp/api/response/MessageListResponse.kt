package com.tce.teacherapp.api.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MessageListResponse(

    var messageList: List<MessageListResponseItem>?,

    val totalUnReadCount :Int

) : Parcelable