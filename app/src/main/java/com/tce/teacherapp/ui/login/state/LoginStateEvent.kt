package com.tce.teacherapp.ui.login.state

import com.tce.teacherapp.util.StateEvent

sealed class LoginStateEvent : StateEvent {

    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ) : LoginStateEvent() {

        override fun errorInfo(): String {
            return "Login attempt failed."
        }

        override fun toString(): String {
            return "LoginStateEvent"
        }
    }

}