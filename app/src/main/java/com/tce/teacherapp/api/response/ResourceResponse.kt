package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Resource

class ResourceResponse(

    @Expose
    val ResourceOriginator: String,

    @Expose
    val TaggingLevel: String,

    @Expose
    val contenttype: String,

    @Expose
    val description: String,

    @Expose
    val id: String,

    @Expose
    val image: String?,

    @Expose
    val skills: List<String>,

    @Expose
    val src: String,

    @Expose
    val title: String,

    @Expose
    val type: String
) {
    fun toResource(
        chapterResourceTypeId: String
    ): Resource {
        return Resource(
            ResourceOriginator = ResourceOriginator,
            TaggingLevel = TaggingLevel,
            contenttype = contenttype,
            description = description,
            id = id,
            image = image,
            skills = skills,
            src = src,
            title = title,
            type = type,
            chapterResourceTypeId = chapterResourceTypeId
        )
    }
}