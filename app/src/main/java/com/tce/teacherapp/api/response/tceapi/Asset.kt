package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Asset(
    @Expose
    val ansKeyId: String,

    @Expose
    val assetId: String,

    @Expose
    val assetType: String,

    @Expose
    val copyright: String,

    @Expose
    val description: String,

    @Expose
    val encryptedFilePath: String,

    @Expose
    val fileName: String,

    @Expose
    val keywords: String,

    @Expose
    val lcmsGradeId: String,

    @Expose
    val lcmsSubjectId: String,

    @Expose
    val mimeType: String,

    @Expose
    val subType: String,

    @Expose
    val thumbFileName: String,

    @Expose
    val title: String,

    @Expose
    val tpId: String,

    @Ignore
    val Mname: String,

    @Ignore
    val Mtype: String,

    @Ignore
    var bValue :Boolean = false

):Parcelable