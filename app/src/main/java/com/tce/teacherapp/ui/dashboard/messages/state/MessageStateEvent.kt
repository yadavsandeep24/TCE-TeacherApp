package com.tce.teacherapp.ui.dashboard.messages.state

import com.tce.teacherapp.util.StateEvent

sealed class MessageStateEvent : StateEvent {

    class GetMessageEvent (
        val query: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting message info."
        }

        override fun toString(): String {
            return "GetMessageEvent"
        }
    }

    class GetStudentEvent (
        val classID:Int,
        val query: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting student info."
        }

        override fun toString(): String {
            return "GetStudentEvent"
        }
    }

    class GetResourceEvent(
        val query: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting resource info."
        }

        override fun toString(): String {
            return "GetResourceEvent"
        }
    }

    object GetResourceTypeEvent: MessageStateEvent(){
        override fun errorInfo(): String {
            return "Error in getting Resource type."
        }

        override fun toString(): String {
            return "GetResourceTypeEvent"
        }
    }

    class GetResourceSelectionEvent(
         val query: String,
        val type: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in resource selection event"
        }

        override fun toString(): String {
            return "GetResourceSelectionEvent"
        }
    }

    class GetMessageConversionEvent(
        val type: String,
        val messageId: String
    ) : MessageStateEvent() {
        override fun errorInfo(): String {
            return "Error in message conversation event"
        }

        override fun toString(): String {
            return "GetMessageConversionEvent"
        }
    }

}