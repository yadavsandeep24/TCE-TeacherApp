package com.tce.teacherapp.ui.login

import com.tce.teacherapp.repository.LoginRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.login.state.LoginFields
import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginViewModel @Inject constructor(val loginRepository: LoginRepository) :
    BaseViewModel<LoginViewState>() {


    override fun handleNewData(data: LoginViewState) {
data.loginFields?.let {
        setLoginData(it)
}
    }

    private fun setLoginData(it: LoginFields) {
        val update = getCurrentViewStateOrNew()
        update.loginFields = it
        setViewState(update)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<LoginViewState>> = when (stateEvent) {

            is LoginStateEvent.LoginAttemptEvent -> {
                loginRepository.attemptLogin(stateEvent.email,stateEvent.password,stateEvent = stateEvent)
            }

            else -> {
                flow {
                    emit(
                        DataState.error<LoginViewState>(
                            response = Response(
                                message = ErrorHandling.INVALID_STATE_EVENT,
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Error(),
                                serviceTypes = RequestTypes.GENERIC
                            ),
                            stateEvent = stateEvent
                        )
                    )
                }
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): LoginViewState {
       return LoginViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}