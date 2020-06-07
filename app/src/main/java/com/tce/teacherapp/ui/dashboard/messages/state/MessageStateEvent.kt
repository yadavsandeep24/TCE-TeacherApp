package com.tce.teacherapp.ui.dashboard.messages.state

import com.tce.teacherapp.util.StateEvent

sealed class MessageStateEvent : StateEvent {

    object GetMessageEvent : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    object GetStudentEvent : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    object GetResourceEvent : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    class GetResourceSelectionEvent(
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