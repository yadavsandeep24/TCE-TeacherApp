package com.tce.teacherapp.ui.dashboard.home.state

import com.tce.teacherapp.util.StateEvent

sealed class DashboardStateEvent : StateEvent {
    class UpdateProfilePic(val resultUri: String) : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in updating profile info."
        }

        override fun toString(): String {
            return "UpdateProfilePic"
        }

    }

    object GetProfileEvent : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting profile info."
        }

        override fun toString(): String {
            return "GetProfileEvent"
        }
    }

    class GetDashboardEvent (val count :Int, val type : String): DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting profile info."
        }

        override fun toString(): String {
            return "GetProfileEvent"
        }
    }


    class GetDashboardEventList (val count :Int): DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting profile info."
        }

        override fun toString(): String {
            return "GetProfileEvent"
        }
    }

    class GetTodayResource (val count :Int): DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting profile info."
        }

        override fun toString(): String {
            return "GetProfileEvent"
        }
    }

    class GetLastViewedResource (val count :Int): DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting profile info."
        }

        override fun toString(): String {
            return "GetProfileEvent"
        }
    }

    object CheckFingerPrintLoginEnabled: DashboardStateEvent(){
        override fun errorInfo(): String {
            return "Error in checking FingerPrint mode."
        }

        override fun toString(): String {
            return "CheckFingerPrintLoginEnabled"
        }

    }
}