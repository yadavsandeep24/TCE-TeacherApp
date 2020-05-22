package com.tce.teacherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.db.entity.*

@Database(
    entities = [Grade::class, Subject::class, Book::class, Node::class, NodeX::class, NodeXX::class, NodeXXX::class],
    version = 1
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getSubjectDao(): SubjectsDao

    companion object {
        const val DATABASE_NAME: String = "tce_db"
    }


}








