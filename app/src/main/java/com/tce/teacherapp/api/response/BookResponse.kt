package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Book
import com.tce.teacherapp.db.entity.Node

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

    private fun toNodeList(bookId: String, nodes: List<NodeResponse>): List<Node> {
        val nodeList: ArrayList<Node> = ArrayList()
        for (bookResponse in nodes) {
            nodeList.add(bookResponse.toNode(bookId))
        }
        return nodeList

    }
}