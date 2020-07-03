package com.tce.teacherapp.ui.dashboard.home.listeners

import com.tce.teacherapp.db.entity.DashboardResourceType

interface LastViewedResourceClickListener {
    fun onLastViewedResourceShowMoreClick(isShowLess : Boolean)
    fun onLastViewedItemClick(dashboardResourceTye : DashboardResourceType)
}