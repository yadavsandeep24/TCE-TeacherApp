package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tce.teacherapp.db.entity.Chapter
import com.tce.teacherapp.db.entity.Topic

class TopicResponse(

    @Expose
    val icon: String?,

    @Expose
    val id: String,

    @Expose
    val index: Int,

    @Expose
    val label: String,

    @Expose
    val menutype: String,

    @Expose
    @SerializedName("node")
    val chapterList: List<ChapterResponse>,

    @Expose
    val type: String,

    @Expose
    val section:String?
) {
    fun toTopic(bookId: String): Topic {
        return Topic(
            icon = icon,
            id = id,
            index = index,
            label = label,
            menutype = menutype,
            type = type,
            chapterList = toChapterList(bookId, id, chapterList),
            bookId = bookId,
            section = section
        )
    }

    private fun toChapterList(
        bookId: String,
        id: String,
        chapters: List<ChapterResponse>
    ): List<Chapter> {
        val chapterList: ArrayList<Chapter> = ArrayList()
        for (chapterResponse in chapters) {
            chapterList.add(chapterResponse.toChapter(bookId, id))
        }
        return chapterList
    }
}