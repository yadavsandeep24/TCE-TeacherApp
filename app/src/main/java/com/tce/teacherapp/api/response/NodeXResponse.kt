package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Chapter
import com.tce.teacherapp.db.entity.NodeXX

class NodeXResponse(

    @Expose
    val icon: String?,

    @Expose
    val id: String,

    @Expose
    val image: String?,

    @Expose
    val label: String,

    @Expose
    val node: List<NodeXXResponse>,

    @Expose
    val type: String
) {
    fun toNodeX(bookId: String, topicId: String): Chapter {
        return Chapter(
            icon = icon,
            id = id,
            image = image,
            label = label,
            node = toNodeXXList(bookId, topicId, id, node),
            type = type,
            topicId = topicId,
            bookId = bookId
        )
    }

    private fun toNodeXXList(
        bookId: String,
        nodeID: String,
        nodeXId: String,
        nodes: List<NodeXXResponse>
    ): List<NodeXX> {
        val nodeXXList: ArrayList<NodeXX> = ArrayList()
        for (nodeXResponse in nodes) {
            nodeXXList.add(nodeXResponse.toNodeXX(bookId, nodeID, nodeXId))
        }
        return nodeXXList
    }
}