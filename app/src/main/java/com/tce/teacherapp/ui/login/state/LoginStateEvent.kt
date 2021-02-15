package com.tce.teacherapp.ui.login.state

import com.tce.teacherapp.util.StateEvent

sealed class LoginStateEvent : StateEvent {

    data class LoginAttemptEvent(
        val schoolName: String,
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

    data class SchoolNameEvent(
        val schoolName: String
    ) : LoginStateEvent() {

        override fun errorInfo(): String {
            return "School name fetch failed."
        }

        override fun toString(): String {
            return "SchoolNameEvent"
        }
    }

    object  ClientIdEvent : LoginStateEvent() {

        override fun errorInfo(): String {
            return "Client id fetch failed."
        }

        override fun toString(): String {
            return "ClientIdEvent"
        }
    }

    object CheckLoginEnabledMode : LoginStateEvent() {
        override fun errorInfo(): String {
            return "Error in checking fingerPrint mode."
        }

        override fun toString(): String {
            return "CheckFingerPrintLoginEnabled"
        }

    }

    object PreUserInfoData : LoginStateEvent() {
        override fun errorInfo(): String {
            return "Error in checking userinfo data."
        }

        override fun toString(): String {
            return "PreUserInfoData"
        }

    }

    class FingerPrintEnableMode(val checked: Boolean) : LoginStateEvent() {

        override fun errorInfo(): String {
            return "Error in setting FingerPrint mode."
        }

        override fun toString(): String {
            return "setLoginMode"
        }
    }

    class FaceIdEnableMode(val checked: Boolean) : LoginStateEvent() {

        override fun errorInfo(): String {
            return "Error in setting FaceId mode."
        }

        override fun toString(): String {
            return "FaceIdEnableMode"
        }
    }

    class ResentOTP(
        val strMobileNo: String
    ) : LoginStateEvent() {
        override fun errorInfo(): String {
            return "Error in sending  otp."
        }

        override fun toString(): String {
            return "ResentOTP"
        }

    }

    class RegisterUserEvent(
        val mobileNo: String,
        val password: String)
        :LoginStateEvent(){
        override fun errorInfo(): String {
            return "Error in registering user  otp."
        }

        override fun toString(): String {
            return "RegisterUserEvent"
        }
    }
}