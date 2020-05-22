package com.tce.teacherapp.ui.dashboard.subjects.state

import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.util.StateEvent

sealed class SubjectStateEvent : StateEvent {

    object GetDivisionEvent : SubjectStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    class DivisionSelectionEvent(
        val grade: Grade,
        val position: Int
    ) : SubjectStateEvent() {
        override fun errorInfo(): String {
            return "Error in division selection event"
        }

        override fun toString(): String {
            return "DivisionSelectionEvent"
        }
    }

    object SubjectSelectionEvent : SubjectStateEvent() {
        override fun errorInfo(): String {
            return "Error in subject selection event"
        }

        override fun toString(): String {
            return "SubjectSelectionEvent"
        }
    }

    class GetSubjectEvent(
        val query: String
    ) : SubjectStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting subject info."
        }

        override fun toString(): String {
            return "GetSubjectEvent"
        }

    }

    class GetTopicEvent(
        val query: String,
        val bookId: String
    ) : SubjectStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting topic info."
        }

        override fun toString(): String {
            return "GetTopicEvent"
        }

    }
}