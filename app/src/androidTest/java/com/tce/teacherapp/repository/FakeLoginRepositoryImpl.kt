package com.tce.teacherapp.repository

import com.tce.teacherapp.ui.login.state.LoginFields
import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeLoginRepositoryImpl
@Inject constructor(): LoginRepository {

    override fun attemptLogin(
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>  = flow{
        resetSession(true)
        emit(
             DataState.data(
                data = LoginViewState(loginFields = LoginFields(email,password)),
                stateEvent = stateEvent,
                response = null
            )
        )

    }

    override fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> {
        TODO("Not yet implemented")
    }

    override fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> {
        TODO("Not yet implemented")
    }

    override fun checkLoginMode(stateEvent: StateEvent): Flow<DataState<LoginViewState>> {
        TODO("Not yet implemented")
    }

    private fun resetSession(new: Boolean) {
    }
}