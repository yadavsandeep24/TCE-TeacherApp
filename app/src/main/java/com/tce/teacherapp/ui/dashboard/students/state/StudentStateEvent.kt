package com.tce.teacherapp.ui.dashboard.students.state

import com.tce.teacherapp.util.StateEvent

sealed class StudentStateEvent : StateEvent {

    class GetStudentEvent (
        val query: String
    ) : StudentStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }


}