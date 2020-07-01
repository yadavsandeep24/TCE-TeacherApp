package com.tce.teacherapp.ui.dashboard.home.listeners

import com.tce.teacherapp.db.entity.ClassListsItem

interface ClassListClickListener {
    fun onClassListItemClick(classListsItem: ClassListsItem)
}