package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.NodeXX
import com.tce.teacherapp.db.entity.NodeXXX

class NodeXXResponse(

    @Expose
    val icon: String?,

    @Expose
    val id: String,

    @Expose
    val label: String,

    @Expose
    val node: List<NodeXXXResponse>,

    @Expose
    val treatment: String?,

    @Expose
    val type: String
) {
    fun toNodeXX(bookId: String, nodeID: String, nodeXId: String): NodeXX {
        return NodeXX(
            icon = icon,
            id = id,
            label = label,
            node = toNodeXXXList(bookId, nodeID, nodeXId, id, node),
            treatment = treatment,
            type = type,
            nodexId = nodeXId,
            nodeId = nodeID,
            bookId = bookId

        )

    }

    private fun toNodeXXXList(
        bookId: String,
        nodeID: String,
        nodeXId: String,
        nodeXXId: String,
        nodes: List<NodeXXXResponse>
    ): List<NodeXXX> {
        val nodeXXXList: ArrayList<NodeXXX> = ArrayList()
        for (nodeXXResponse in nodes) {
            nodeXXXList.add(nodeXXResponse.toNodeXXX(bookId, nodeID, nodeXId, nodeXXId))
        }
        return nodeXXXList
    }
}