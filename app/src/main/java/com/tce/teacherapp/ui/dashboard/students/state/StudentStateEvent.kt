package com.tce.teacherapp.ui.dashboard.students.state

import com.tce.teacherapp.util.StateEvent

sealed class StudentStateEvent : StateEvent {

    object GetStudentEvent : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting student info."
        }

        override fun toString(): String {
            return "GetStudentEvent"
        }
    }

    object GetAttendanceData : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting attendance info."
        }

        override fun toString(): String {
            return "GetAttendanceData"
        }
    }

    object GetFeedbackMaster : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting attendance info."
        }

        override fun toString(): String {
            return "GetAttendanceData"
        }
    }

    object GetStudentPortfolio : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting StudentPortfolio info."
        }

        override fun toString(): String {
            return "GetStudentPortfolio"
        }
    }

    object GetGalleryData : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting GalleryData info."
        }

        override fun toString(): String {
            return "GetGalleryData"
        }
    }

}