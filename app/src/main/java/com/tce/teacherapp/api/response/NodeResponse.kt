package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Topic
import com.tce.teacherapp.db.entity.Chapter

class NodeResponse(

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
    val node: List<NodeXResponse>,

    @Expose
    val type: String,

    @Expose
    val section:String?
) {
    fun toNode(bookId: String): Topic {
        return Topic(
            icon = icon,
            id = id,
            index = index,
            label = label,
            menutype = menutype,
            type = type,
            node = toNodeXList(bookId, id, node),
            bookId = bookId,
            section = section
        )
    }

    private fun toNodeXList(
        bookId: String,
        id: String,
        nodes: List<NodeXResponse>
    ): List<Chapter> {
        val nodeXList: ArrayList<Chapter> = ArrayList()
        for (nodeXResponse in nodes) {
            nodeXList.add(nodeXResponse.toNodeX(bookId, id))
        }
        return nodeXList
    }
}