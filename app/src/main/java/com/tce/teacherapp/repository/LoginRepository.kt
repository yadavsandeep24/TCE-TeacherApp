package com.tce.teacherapp.repository

import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun getAPISessionTimeOut():String

    fun attemptLogin(
        schoolName : String?,
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

    fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

    fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

    fun checkLoginMode(stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

    fun getPreUserData(stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

    fun setQuickAccessScreen(isShow: Boolean)

    fun resentOTP(
        strMobileNo: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

    fun registerUser(
        mobileNo: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

    fun getSchoolList(
        schoolName: String,
        stateEvent: LoginStateEvent
    ): Flow<DataState<LoginViewState>>

    fun getClientID(stateEvent: LoginStateEvent):Flow<DataState<LoginViewState>>
    fun tceLogin(
        schoolName: String,
        email: String,
        password: String,
        stateEvent: LoginStateEvent
    ): Flow<DataState<LoginViewState>>

    fun extendToken(): Boolean

}
