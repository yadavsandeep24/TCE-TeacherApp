package com.tce.teacherapp.util

import android.util.Log
import com.tce.teacherapp.BuildConfig.DEBUG
import com.tce.teacherapp.util.Constants.Companion.TAG

fun printLogD(className: String?, message: String ) {
    if (DEBUG) {
        Log.d(TAG, "$className: $message")
    }
}