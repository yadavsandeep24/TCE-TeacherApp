package com.tce.teacherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.db.dao.UserDao
import com.tce.teacherapp.db.entity.*

@Database(
    entities = [
        Grade::class,
        Subject::class,
        Book::class,
        Topic::class,
        Chapter::class,
        NodeXX::class,
        NodeXXX::class,
        Profile::class
    ], version = 3)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getSubjectDao(): SubjectsDao

    abstract fun getUserDao():  UserDao

    companion object {
        const val DATABASE_NAME: String = "tce_db"
    }


}








