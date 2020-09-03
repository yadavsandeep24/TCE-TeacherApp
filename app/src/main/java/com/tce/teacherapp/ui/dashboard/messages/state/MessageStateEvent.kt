package com.tce.teacherapp.ui.dashboard.messages.state

import com.tce.teacherapp.util.StateEvent

sealed class MessageStateEvent : StateEvent {

    class GetMessageEvent (
        val query: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    class GetStudentEvent (
        val classID:Int,
        val query: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    class GetResourceEvent(
        val query: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    class GetResourceSelectionEvent(
         val query: String,
        val typeId: Int
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in division selection event"
        }

        override fun toString(): String {
            return "DivisionSelectionEvent"
        }
    }

    class GetMessageConversionEvent(
        val query: String,
        val messageId: Int
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in division selection event"
        }

        override fun toString(): String {
            return "DivisionSelectionEvent"
        }
    }

}