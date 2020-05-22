package com.tce.teacherapp.ui.auth.state

import com.tce.teacherapp.util.StateEvent

sealed class AuthStateEvent : StateEvent {

    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent() {

        override fun errorInfo(): String {
            return "Login attempt failed."
        }

        override fun toString(): String {
            return "LoginStateEvent"
        }
    }

}