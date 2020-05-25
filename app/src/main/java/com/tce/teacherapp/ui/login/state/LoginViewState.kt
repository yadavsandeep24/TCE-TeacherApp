package com.tce.teacherapp.ui.login.state

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val LOGIN_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.LoginViewState"

@Parcelize
data class LoginViewState(
    var loginFields: LoginFields? = null
) : Parcelable

@Parcelize
data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
) : Parcelable

