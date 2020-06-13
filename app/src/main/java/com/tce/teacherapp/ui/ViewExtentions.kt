package com.tce.teacherapp.ui

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import com.tce.teacherapp.util.StateMessageCallback

private val TAG: String = "AppDebug"

fun Activity.displayToast(
    message: String,
    stateMessageCallback: StateMessageCallback
) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}


interface AreYouSureCallback {

    fun proceed()

    fun cancel()
}




