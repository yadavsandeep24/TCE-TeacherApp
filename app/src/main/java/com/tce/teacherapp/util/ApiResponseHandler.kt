package com.tce.teacherapp.util

import com.tce.teacherapp.util.ErrorHandling.Companion.NETWORK_ERROR
import org.json.JSONObject


abstract class ApiResponseHandler<ViewState, Data> (
    private val response: ApiResult<Data?>,
    private val stateEvent: StateEvent?
) {

    private val TAG: String = "AppDebug"

    suspend fun getResult(): DataState<ViewState> {

        return when (response) {

            is ApiResult.GenericError -> {
                if(response.code == 403){
                    val json = JSONObject(response.errorMessage.toString())
                    val erroMessage = json.getString("errorMessage")
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()}\n\nReason: $erroMessage",
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.AccessDenied(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                }else {
                    val json = JSONObject(response.errorMessage.toString())
                    val erroMessage = json.getString("errorMessage")
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()}\n\nReason: $erroMessage",
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                }
            }

            is ApiResult.NetworkError -> {
                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()}\n\nReason: ${NETWORK_ERROR}",
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error(),
                        serviceTypes = RequestTypes.GENERIC
                    ),
                    stateEvent = stateEvent
                )
            }

            is ApiResult.Success -> {
                if (response.value == null) {
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()}\n\nReason: Data is NULL.",
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                } else {
                    handleSuccess(resultObj = response.value)
                }
            }

        }
    }

    abstract suspend fun handleSuccess(resultObj: Data): DataState<ViewState>
    //abstract suspend fun handleError(error: ApiResult.GenericError): DataState<ViewState>

}