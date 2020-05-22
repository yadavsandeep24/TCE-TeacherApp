package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Node
import com.tce.teacherapp.db.entity.NodeX

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
    val type: String
) {
    fun toNode(bookId: String): Node {
        return Node(
            icon = icon,
            id = id,
            index = index,
            label = label,
            menutype = menutype,
            type = type,
            node = toNodeXList(bookId, id, node),
            bookId = bookId
        )
    }

    private fun toNodeXList(
        bookId: String,
        id: String,
        nodes: List<NodeXResponse>
    ): List<NodeX> {
        val nodeXList: ArrayList<NodeX> = ArrayList()
        for (nodeXResponse in nodes) {
            nodeXList.add(nodeXResponse.toNodeX(bookId, id))
        }
        return nodeXList
    }
}