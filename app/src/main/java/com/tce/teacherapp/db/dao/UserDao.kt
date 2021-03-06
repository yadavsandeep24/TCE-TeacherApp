package com.tce.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tce.teacherapp.db.entity.Profile

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userInfo: Profile): Long

    @Query("SELECT * FROM UserInfo WHERE email = :userName AND password = :password")
    fun getUserInfoData(userName: String,password:String): Profile?

    @Query("UPDATE UserInfo SET imageurl = :imageUrl WHERE user_id = :pk")
    fun updateProfilePic(imageUrl:String,pk: String): Int

    @Query("UPDATE UserInfo SET fingerprint_mode_enabled = :mode WHERE user_id = :pk")
    fun updateFingerPrintLoginMode(mode:Boolean,pk: String): Int

    @Query("UPDATE UserInfo SET face_mode_enabled = :mode WHERE user_id = :pk")
    fun updateFaceIdLoginMode(mode:Boolean,pk: String): Int

    @Query("UPDATE UserInfo SET password = :newPassword WHERE user_id = :userId")
    fun updatePassword(newPassword: String, userId: String)

    @Query("SELECT password FROM UserInfo WHERE user_id = :userId")
    fun getOldPassword(userId: String): String


    @Query("SELECT * FROM UserInfo WHERE user_id = :userId")
    fun getUserByUserId(userId: String): Profile?
}