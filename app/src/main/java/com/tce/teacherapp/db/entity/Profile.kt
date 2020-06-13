package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    @Expose
    val id: Int,

    @Expose
    val name: String,

    @Expose
    val relationship: String,

    @Expose
    val contact: String,

    @Expose
    val email: String,

    @Expose
    val password: String,

    @Expose
    val address: String,

    @Expose
    val subjects: String,

    @Expose
    val imageUrl : String
): Parcelable
