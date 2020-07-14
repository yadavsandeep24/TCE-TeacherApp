package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tce.teacherapp.db.entity.ChapterResourceType
import com.tce.teacherapp.db.entity.Resource

class ChapterResourceTypeResponse(

    @Expose
    val icon: String?,

    @Expose
    val id: String,

    @Expose
    val label: String,

    @Expose
    @SerializedName("node")
    val resourceList: List<ResourceResponse>,

    @Expose
    val treatment: String?,

    @Expose
    val type: String
) {
    fun toChapterResourceType(bookId: String, topicId: String, chapterId: String): ChapterResourceType {
        return ChapterResourceType(
            icon = icon,
            id = id,
            label = label,
            resourceList = toResourceList(id, resourceList),
            treatment = treatment,
            type = type,
            chapterId = chapterId,
            topicId = topicId,
            bookId = bookId

        )

    }

    private fun toResourceList(
        chapterResourceTypeId: String,
        resources: List<ResourceResponse>
    ): List<Resource> {
        val resourceList: ArrayList<Resource> = ArrayList()
        for (resourceResponse in resources) {
            resourceList.add(resourceResponse.toResource(chapterResourceTypeId))
        }
        return resourceList
    }
}