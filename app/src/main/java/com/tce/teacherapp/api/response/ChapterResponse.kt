package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tce.teacherapp.db.entity.Chapter
import com.tce.teacherapp.db.entity.ChapterResourceType

class ChapterResponse(

    @Expose
    val icon: String?,

    @Expose
    val id: String,

    @Expose
    val image: String?,

    @Expose
    val label: String,

    @Expose
    @SerializedName("node")
    val chapterResourceTypeList: List<ChapterResourceTypeResponse>,

    @Expose
    val type: String
) {
    fun toChapter(bookId: String, topicId: String): Chapter {
        return Chapter(
            icon = icon,
            id = id,
            image = image,
            label = label,
            chapterResourceTypeList = toChapterResourceTypeList(bookId, topicId, id, chapterResourceTypeList),
            type = type,
            topicId = topicId,
            bookId = bookId
        )
    }

    private fun toChapterResourceTypeList(
        bookId: String,
        topicID: String,
        chapterId: String,
        chapterResourceTypes: List<ChapterResourceTypeResponse>
    ): List<ChapterResourceType> {
        val chapterResourceTypeList: ArrayList<ChapterResourceType> = ArrayList()
        for (chapterResourceTypeResponse in chapterResourceTypes) {
            chapterResourceTypeList.add(chapterResourceTypeResponse.toChapterResourceType(bookId, topicID, chapterId))
        }
        return chapterResourceTypeList
    }
}