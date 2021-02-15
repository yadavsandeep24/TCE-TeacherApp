package com.tce.teacherapp.api.response.tceapi

import com.google.gson.annotations.Expose

class TCETopicResponse {

    @Expose
    val topicList = ArrayList<TCETopicResponseItem>()
}