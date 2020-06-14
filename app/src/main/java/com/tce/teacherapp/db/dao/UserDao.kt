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
}