package com.tce.teacherapp.ui.dashboard.students.state

import com.tce.teacherapp.util.StateEvent

sealed class StudentStateEvent : StateEvent {

    class GetStudentEvent(
        val classID:Int,
        val query: String
    ) : StudentStateEvent() {
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

    class GetStudentPortfolio(val type:Int) : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting StudentPortfolio info."
        }

        override fun toString(): String {
            return "GetStudentPortfolio"
        }
    }

    class GetGalleryData(
        val type :Int
    ) : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting GalleryData info."
        }

        override fun toString(): String {
            return "GetGalleryData"
        }
    }

}