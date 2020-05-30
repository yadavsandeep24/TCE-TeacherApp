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


}