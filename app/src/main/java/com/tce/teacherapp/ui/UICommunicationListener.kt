package com.tce.teacherapp.ui

import com.tce.teacherapp.util.Response
import com.tce.teacherapp.util.StateMessageCallback

interface UICommunicationListener {

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

    fun displayProgressBar(isLoading: Boolean)

    fun expandAppBar(value: Boolean)

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean

    fun onBackPressed()
}