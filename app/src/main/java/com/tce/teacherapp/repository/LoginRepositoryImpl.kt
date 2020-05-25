package com.tce.teacherapp.repository

import android.content.SharedPreferences
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.ui.login.state.LoginFields
import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.PreferenceKeys
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
class LoginRepositoryImpl
@Inject
        constructor(
    val tceService: TCEService,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): LoginRepository {

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

    private fun resetSession(new: Boolean) {
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES,new).commit()
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS,new).commit()
    }
}