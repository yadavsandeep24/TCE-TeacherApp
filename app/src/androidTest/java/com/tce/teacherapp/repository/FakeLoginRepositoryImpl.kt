package com.tce.teacherapp.repository

import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeLoginRepositoryImpl
@Inject constructor(): LoginRepository {

    override fun attemptLogin(
        schoolName: String?,
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> {
        TODO("Not yet implemented")
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

    override fun getPreUserData(stateEvent: StateEvent): Flow<DataState<LoginViewState>> {
        TODO("Not yet implemented")
    }

    override fun setQuickAccessScreen(isShow: Boolean) {
        TODO("Not yet implemented")
    }

    override fun resentOTP(
        strMobileNo: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> {
        TODO("Not yet implemented")
    }

    override fun registerUser(
        mobileNo: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> {
        TODO("Not yet implemented")
    }

    private fun resetSession(new: Boolean) {
    }
}