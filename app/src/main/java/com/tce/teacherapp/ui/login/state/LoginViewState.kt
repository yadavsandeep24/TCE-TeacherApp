package com.tce.teacherapp.ui.login.state

import android.os.Parcelable
import com.tce.teacherapp.api.response.tceapi.ClientIDResponse
import com.tce.teacherapp.db.entity.Profile
import com.tce.teacherapp.util.MessageConstant
import kotlinx.android.parcel.Parcelize
import java.util.regex.Matcher
import java.util.regex.Pattern

const val LOGIN_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.LoginViewState"

@Parcelize
data class LoginViewState(
    var loginFields: LoginFields? = null,
    var isFingerPrintLoginEnabled: Boolean? = null,
    var isFaceLoginEnabled: Boolean? = null,
    var profile: Profile? = null,
    var registerFields: RegisterUserFields? = null,
    var SchoolResponse: List<String>? = null,
    var clientIdRes:ClientIDResponse?= null
) : Parcelable

@Parcelize
data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null,
    val login_schoolName: String? = null,
    var login_mode_fingePrint_Enabled: Boolean? = null,
    var login_mode_faceId_Enabled: Boolean? = null,
    var isQuickAccessScreenShow: Boolean? = null
) : Parcelable {
    class LoginError {

        companion object {

            fun mustFillAllFields(): String {
                return MessageConstant.LOGIN_MANDATORY_FIELD
            }

            fun none(): String {
                return "None"
            }

        }
    }

    fun isValidForLogin(): String {
        if (login_email != null && login_email!!.isEmpty()) {
            return LoginError.mustFillAllFields()
        } else if (login_password != null && login_password!!.isEmpty()) {
            return LoginError.mustFillAllFields()
        } else if (login_schoolName != null && login_schoolName.isEmpty()) {
            return LoginError.mustFillAllFields()
        }

        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(email=$login_email, password=$login_password)"
    }
}

@Parcelize
data class ForgetPasswordFields(
    var mobile_no: String? = null
) : Parcelable {
    class LoginError {

        companion object {

            fun mustFillAllFields(): String {
                return MessageConstant.LOGIN_MANDATORY_FIELD
            }

            fun none(): String {
                return "None"
            }

        }
    }

    fun isValidMobileNo(): String {
        if (mobile_no != null && mobile_no!!.isEmpty()) {
            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }
}

@Parcelize
data class RegisterUserFields(
    var mobile_no: String? = null,
    var password: String? = null
) : Parcelable {
    class LoginError {

        companion object {

            fun mustFillAllFields(): String {
                return MessageConstant.LOGIN_MANDATORY_FIELD
            }

            fun isValidPassword(): String {
                return MessageConstant.UPDATE_PASSWORD_VALIDATION
            }

            fun none(): String {
                return "None"
            }

        }
    }

    fun checkValidRegistration(): String {
        if (password != null) {
            val pattern: Pattern
            val matcher: Matcher

            val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[a-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"

            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(password)
            if (!matcher.matches()) {
                return LoginError.isValidPassword()
            }
        }

        return LoginError.none()
    }
}
