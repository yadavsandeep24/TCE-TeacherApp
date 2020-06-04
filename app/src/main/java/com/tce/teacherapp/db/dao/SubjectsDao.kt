package com.tce.teacherapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tce.teacherapp.db.entity.*

@Dao
interface SubjectsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: Grade): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: Node): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserNodeX(nodex: NodeX): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodeXX(nodexx: NodeXX): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodeXXX(nodexxx: NodeXXX): Long

    @Query("SELECT * FROM Grade ORDER BY order_no ASC")
    fun getGradeListData(): List<Grade>

    @Query("SELECT * FROM Subject WHERE grade_id = :gradeId ORDER BY subject_index ASC")
    fun getSubjectListData(gradeId: String): List<Subject>

    @Query("SELECT * FROM Subject WHERE grade_id = :gradeId AND title LIKE '%' || :query || '%' ORDER BY subject_index ASC")
    fun getSubjectListData(query: String, gradeId: String): List<Subject>

    @Query("SELECT * FROM Node where book_id = :bookId ORDER BY `index` ASC")
    fun getTopicListData(bookId: String): List<Node>

    @Query("SELECT * FROM Node where book_id = :bookId AND label LIKE '%' || :query || '%' ORDER BY `index` ASC")
    fun getTopicListData(query: String, bookId: String): List<Node>

}

















