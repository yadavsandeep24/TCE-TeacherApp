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
            return "LoginAttemptEvent"
        }
    }

    object CheckLoginEnabledMode: LoginStateEvent() {
        override fun errorInfo(): String {
            return "Error in checking FingerPrint mode."
        }

        override fun toString(): String {
            return "CheckFingerPrintLoginEnabled"
        }

    }

    class FingerPrintEnableMode(val checked: Boolean): LoginStateEvent() {

        override fun errorInfo(): String {
            return "Error in setting FingerPrint mode."
        }

        override fun toString(): String {
            return "setLoginMode"
        }
    }

    class FaceIdEnableMode(val checked: Boolean): LoginStateEvent() {

        override fun errorInfo(): String {
            return "Error in setting FaceId mode."
        }

        override fun toString(): String {
            return "FaceIdEnableMode"
        }
    }
}