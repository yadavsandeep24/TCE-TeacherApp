package com.tce.teacherapp.ui.home.listeners

import com.tce.teacherapp.db.entity.Student

interface ChildClickListener {
    fun onChildListItemClick(student : Student)
}