package com.tce.teacherapp.repository

import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    fun attemptLogin(
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>

}