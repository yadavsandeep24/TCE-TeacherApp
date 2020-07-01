package com.tce.teacherapp.ui.dashboard.home.listeners

interface EventClickListener {
    fun onEventShowMoreClick(isShowLess : Boolean)
    fun onEventItemClick(type : String)
}