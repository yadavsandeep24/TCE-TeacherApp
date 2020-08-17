package com.tce.teacherapp.ui.dashboard.students.interfaces

import com.tce.teacherapp.api.response.StudentListResponseItem

interface ViewHolderClickListener {
    fun onLongTap(index : Int)
    fun onTap(index : Int,item: StudentListResponseItem?)
}