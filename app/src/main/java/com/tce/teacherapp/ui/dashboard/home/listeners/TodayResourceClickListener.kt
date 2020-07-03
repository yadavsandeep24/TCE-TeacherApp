package com.tce.teacherapp.ui.dashboard.home.listeners

import com.tce.teacherapp.db.entity.DashboardResourceType

interface TodayResourceClickListener {
    fun onTodayResourceShowMoreClick(isShowLess : Boolean)
    fun onTodayResourceItemClick(dashboardResourceType: DashboardResourceType)
}