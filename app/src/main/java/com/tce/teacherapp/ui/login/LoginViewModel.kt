package com.tce.teacherapp.ui.login

import com.tce.teacherapp.db.entity.Profile
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
        data.isFingerPrintLoginEnabled?.let {
            setFingerPrintEnableData(it)
        }
        data.isFaceLoginEnabled?.let {
            setFaceIdEbableddata(it)
        }

        setProfileData(data.profile)
    }

    private fun setProfileData(it: Profile?) {
        val update = getCurrentViewStateOrNew()
        update.profile = it
        setViewState(update)
    }

    private fun setFingerPrintEnableData(it: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.isFingerPrintLoginEnabled = it
        setViewState(update)
    }
    private fun setFaceIdEbableddata(it: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.isFaceLoginEnabled = it
        setViewState(update)
    }
    private fun setLoginData(it: LoginFields) {
        val update = getCurrentViewStateOrNew()
        update.loginFields = it
        setViewState(update)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<LoginViewState>> = when (stateEvent) {

            is LoginStateEvent.LoginAttemptEvent -> {
                loginRepository.attemptLogin(
                    stateEvent.email,
                    stateEvent.password,
                    stateEvent = stateEvent
                )
            }
           is LoginStateEvent.CheckLoginEnabledMode ->{
                loginRepository.checkLoginMode(stateEvent = stateEvent)
            }
            is LoginStateEvent.FingerPrintEnableMode -> {
                loginRepository.setFingerPrintMode(stateEvent.checked,stateEvent =  stateEvent)
            }
            is LoginStateEvent.FaceIdEnableMode -> {
                loginRepository.setFaceIdMode(stateEvent.checked,stateEvent =  stateEvent)
            }
            is LoginStateEvent.PreUserInfoData ->{
                loginRepository.getPreUserData(stateEvent = stateEvent)
            }
            else -> {
                flow {
                    emit(
                        DataState.error<LoginViewState>(
                            response = Response(
                                message = ErrorHandling.INVALID_STATE_EVENT,
                                uiComponentType = UIComponentType.None,
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

    fun setQuickAccessScreenShow(isShow: Boolean) {
        loginRepository.setQuickAccessScreen(isShow)
    }


}