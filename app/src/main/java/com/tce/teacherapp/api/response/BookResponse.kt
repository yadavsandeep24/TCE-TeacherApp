package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tce.teacherapp.db.entity.Book
import com.tce.teacherapp.db.entity.Topic

class BookResponse(
    @Expose
    val id: String,

    @Expose
    val label: String,

    @Expose
    @SerializedName("node")
    val topicList: List<TopicResponse>,

    @Expose
    val type: String
) {
    fun toBook(): Book {
        return Book(
            id = id,
            label = label,
            type = type,
            topicList = toTopicList(id, topicList)
        )
    }

    private fun toTopicList(bookId: String, topics: List<TopicResponse>): List<Topic> {
        val topicList: ArrayList<Topic> = ArrayList()
        for (bookResponse in topics) {
            topicList.add(bookResponse.toTopic(bookId))
        }
        return topicList

    }
}