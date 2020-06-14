package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "UserInfo")
@Parcelize
data class Profile(
    @Expose
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_id")
    val id: String,

    @Expose
    @ColumnInfo(name = "user_name")
    val name: String,

    @Expose
    @ColumnInfo(name = "relationship")
    val relationship: String,

    @Expose
    @ColumnInfo(name = "contact")
    val contact: String,

    @Expose
    @ColumnInfo(name = "email")
    val email: String,

    @Expose
    @ColumnInfo(name = "password")
    val password: String,

    @Expose
    @ColumnInfo(name = "address")
    val address: String,

    @Expose
    @ColumnInfo(name = "subjects")
    val subjects: String,

    @Expose
    @ColumnInfo(name = "imageurl")
    val imageUrl : String
): Parcelable
