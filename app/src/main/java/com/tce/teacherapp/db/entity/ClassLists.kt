package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ClassLists(

    @Expose
    val classList : List<ClassListsItem>

): Parcelable