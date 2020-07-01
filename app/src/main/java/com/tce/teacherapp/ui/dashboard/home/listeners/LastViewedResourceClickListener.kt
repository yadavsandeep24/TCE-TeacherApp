package com.tce.teacherapp.ui.dashboard.home.listeners

interface LastViewedResourceClickListener {
    fun onLastViewedResourceShowMoreClick(isShowLess : Boolean)
    fun onLastViewedItemClick(title : String)
}