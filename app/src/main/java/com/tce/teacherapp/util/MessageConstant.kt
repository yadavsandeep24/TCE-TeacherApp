package com.tce.teacherapp.util

interface MessageConstant {
    companion object {
        const val LOGIN_MANDATORY_FIELD = "All the fields are required."

        const val LOGIN_DEFAULT_SCHOOLNAME = "ECE"
        const val LOGIN_DEFAULT_USERNAME = "school.admin"
        const val LOGIN_DEFAULT_PASSWORD = "123"

        const val RESPONSE_PASSWORD_UPDATE_SUCCESS = "Password changed successfully."
        const val UPDATE_PASSWORD_NEW_CONFIRM_MISMATCH ="Both new password should match."
        const val UPDATE_PASSWORD_VALIDATION ="New password must contain:\nMinimum eight characters,\nat least one uppercase letter," +
                "\nat least one lowercase letter,\none special character."

    }
}