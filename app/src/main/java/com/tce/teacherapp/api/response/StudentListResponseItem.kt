package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StudentListResponseItem(

    @Expose
    val Address: String,

    @Expose
    val BloodGroup: String,

    @Expose
    val DOB: String,

    @Expose
    val ImagePath: String,

    @Expose
    val Name: String,

    @Expose
    val ParentList: List<Parent>,

    @Expose
    val ProgressCard: List<ProgressCard>,

    @Expose
    val Term1ReportStatus: Boolean,

    @Expose
    val Term2ReportStatus: Boolean,

    @Expose
    val studentClass: String,

    @Expose
    val grade_division_id: String,

    @Expose
    val id: String,

    @Expose
    val school: String,

    @Expose
    val teacher: String
):Parcelable