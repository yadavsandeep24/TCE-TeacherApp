package com.tce.teacherapp.ui.dashboard.planner.listeners

import com.tce.teacherapp.db.entity.Event

interface EventClickListener {
    fun onEventShowMoreClick(isShowLess : Boolean)
    fun onEventItemClick(event : Event)
}