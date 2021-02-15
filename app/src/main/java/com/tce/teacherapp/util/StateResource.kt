package com.tce.teacherapp.util

import com.tce.teacherapp.ui.AreYouSureCallback

data class StateMessage(val response: Response)

data class Response(
    val message: String?,
    val uiComponentType: UIComponentType,
    val messageType: MessageType,
    val serviceTypes: RequestTypes
)

sealed class UIComponentType {

    object Toast : UIComponentType()

    object Dialog : UIComponentType()

    class AreYouSureDialog(
        val callback: AreYouSureCallback
    ) : UIComponentType()

    object None : UIComponentType()
}

sealed class MessageType {

    class Success : MessageType()

    class Error : MessageType()

    class Info : MessageType()

    class None : MessageType()

    class AccessDenied : MessageType()
}


interface StateMessageCallback {

    fun removeMessageFromStack()
}
