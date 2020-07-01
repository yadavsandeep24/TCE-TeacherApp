package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Book
import com.tce.teacherapp.db.entity.Topic

class BookResponse(
    @Expose
    val id: String,

    @Expose
    val label: String,

    @Expose
    val node: List<NodeResponse>,

    @Expose
    val type: String
) {
    fun toBook(): Book {
        return Book(
            id = id,
            label = label,
            type = type,
            node = toNodeList(id, node)
        )
    }

    private fun toNodeList(bookId: String, nodes: List<NodeResponse>): List<Topic> {
        val nodeList: ArrayList<Topic> = ArrayList()
        for (bookResponse in nodes) {
            nodeList.add(bookResponse.toNode(bookId))
        }
        return nodeList

    }
}