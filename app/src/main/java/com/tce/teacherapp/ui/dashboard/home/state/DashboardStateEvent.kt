package com.tce.teacherapp.ui.dashboard.home.state

import com.tce.teacherapp.db.entity.Profile
import com.tce.teacherapp.util.StateEvent

sealed class DashboardStateEvent : StateEvent {
    class UpdateProfilePic(val resultUri: String) : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in updating profile pic info."
        }

        override fun toString(): String {
            return "UpdateProfilePic"
        }
    }

    class UpdateProfile(val userInfo: Profile) : DashboardStateEvent() {
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

    object GetDashboardData : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting profile info."
        }

        override fun toString(): String {
            return "GetProfileEvent"
        }
    }

    class GetDashboardEvent(
        val count: Int,
        val showOriginal: Boolean
    ) : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting event info."
        }

        override fun toString(): String {
            return "GetDashboardEvent"
        }
    }


    class GetTodayResource(
        val count: Int,
        val showOriginal: Boolean
    ) : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting today resource info."
        }

        override fun toString(): String {
            return "GetTodayResource"
        }
    }

    class GetLastViewedResource(
        val count: Int,
        val showOriginal: Boolean
    ) : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting last view resource info."
        }

        override fun toString(): String {
            return "GetLastViewedResource"
        }
    }


    class FingerPrintEnableMode(val checked: Boolean) : DashboardStateEvent() {

        override fun errorInfo(): String {
            return "Error in setting FingerPrint mode."
        }

        override fun toString(): String {
            return "dashbard_setLoginMode"
        }
    }


    class FaceIdEnableMode(val checked: Boolean) : DashboardStateEvent() {

        override fun errorInfo(): String {
            return "Error in setting FaceId mode."
        }

        override fun toString(): String {
            return "Dashboard+FaceIdEnableMode"
        }
    }

    class UpdatePassword(
        val oldPassword: String,
        val newPassword: String,
        val conFirmPassword: String
    ) : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in setting password mode."
        }

        override fun toString(): String {
            return "Dashboard+FaceIdEnableMode"
        }

    }


    object CheckLoginEnabledMode : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in checking FingerPrint mode."
        }

        override fun toString(): String {
            return "d_CheckFingerPrintLoginEnabled"
        }

    }

    object GetUserClassList : DashboardStateEvent() {
        override fun errorInfo(): String {
            return "Error in checking class info."
        }

        override fun toString(): String {
            return "GetUserClassList"
        }

    }
}