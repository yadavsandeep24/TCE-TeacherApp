package com.tce.teacherapp.ui.dashboard.home.state

import com.tce.teacherapp.util.StateEvent

sealed class DashboardStateEvent : StateEvent {

    object GetProfileEvent : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting profile info."
        }

        override fun toString(): String {
            return "GetProfileEvent"
        }
    }
}