package com.tce.teacherapp.ui.dashboard.home.state

import android.os.Parcelable
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.db.entity.*
import com.tce.teacherapp.util.MessageConstant
import kotlinx.android.parcel.Parcelize
import java.util.regex.Matcher
import java.util.regex.Pattern


const val DASHBOARD_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class DashboardViewState (
    var profile: Profile?= null,
    var isFingerPrintLoginEnabled: Boolean? = null,
    var isFaceLoginEnabled: Boolean? = null,
    var eventData : EventData? = null,
    var todayResourceData: TodaysResourceData? = null,
    var lastViewedResourceData : LastViewResourceData? = null,
    var isPasswordUpdated: Boolean? = null,
    var classList: List<ClassListsItem>? = null,
    var latestUpdateList : ArrayList<DashboardLatestUpdate>? =null,
    var childList : ArrayList<StudentListResponseItem>? =null
) : Parcelable


@Parcelize
data class UpdatePasswordFields(
    var old_password: String? = null,
    var new_password: String? = null,
    var confirm_password: String? = null
) : Parcelable{
    class LoginError {

        companion object {

            fun mustFillAllFields(): String{
                return MessageConstant.LOGIN_MANDATORY_FIELD
            }
            fun isValidPassword() : String{
                return MessageConstant.UPDATE_PASSWORD_VALIDATION
            }

            fun none():String{
                return "None"
            }

        }
    }
    fun checkValidPassword(): String {
        if(new_password != null) {
            val pattern: Pattern
            val matcher: Matcher

            val PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[a-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"

            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(new_password)
            if(!matcher.matches()) {
                return LoginError.isValidPassword()
            }
        }

        return LoginError.none()
    }
}