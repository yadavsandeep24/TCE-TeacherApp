package com.tce.teacherapp.ui.login.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.Profile
import com.tce.teacherapp.util.MessageConstant
import kotlinx.android.parcel.Parcelize

const val LOGIN_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.LoginViewState"

@Parcelize
data class LoginViewState(
    var loginFields: LoginFields? = null,
    var isFingerPrintLoginEnabled: Boolean? = null,
    var isFaceLoginEnabled: Boolean? = null,
    var profile: Profile?= null
) : Parcelable

@Parcelize
data class LoginFields(
    val login_schoolName: String? = null,
    var login_email: String? = null,
    var login_password: String? = null,
   var login_mode_fingePrint_Enabled:Boolean? = null,
   var login_mode_faceId_Enabled:Boolean? = null,
    var isQuickAccessScreenShow: Boolean? = null
) : Parcelable{
    class LoginError {

        companion object {

            fun mustFillAllFields(): String{
                return MessageConstant.LOGIN_MANDATORY_FIELD
            }

            fun none():String{
                return "None"
            }

        }
    }
    fun isValidForLogin(): String{

        if(login_schoolName.isNullOrEmpty() ||
            login_email.isNullOrEmpty() ||
            login_password.isNullOrEmpty()){

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(email=$login_email, password=$login_password)"
    }
}

