package com.tce.teacherapp.ui.dashboard.home.listeners

import com.tce.teacherapp.api.response.StudentListResponseItem

interface ChildClickListener {
    fun onChildListItemClick(student: StudentListResponseItem)
}