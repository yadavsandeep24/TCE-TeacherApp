package com.tce.teacherapp.ui.dashboard.home.listeners

import com.tce.teacherapp.db.entity.Event

/**
 * Created by Sandeep Y. on 1/7/2020.
 */
interface EventClickListener {
    fun onEventListItemClick(event : Event)
    fun onLocationClickListener()
}
