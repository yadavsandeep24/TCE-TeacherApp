package com.tce.teacherapp.ui.dashboard.home.listeners

import com.tce.teacherapp.db.entity.DashboardLatestUpdate
import com.tce.teacherapp.db.entity.Event

interface LatestUpdateClickListeners {
    fun onMessageClickListener(daashboardLatestUpdate : DashboardLatestUpdate)
    fun onViewPlannerClick()
    fun onLatestUpdateEventClick(event : Event)
}