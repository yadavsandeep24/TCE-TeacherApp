package com.tce.teacherapp.repository

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.db.dao.UserDao
import com.tce.teacherapp.db.entity.UserInfo
import com.tce.teacherapp.ui.login.state.LoginFields
import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.PreferenceKeys
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FlowPreview
class LoginRepositoryImpl
@Inject
constructor(
    val userDao: UserDao,
    val tceService: TCEService,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor,
    val application: Application
): LoginRepository {

    override fun attemptLogin(
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>>  = flow{
        resetSession(true)

        withContext(IO){
            val userInfo = userDao.getUserInfoData(email,password)
            if(userInfo != null)
            {
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,email).commit()
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,password).commit()
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,userInfo.id).commit()
            }else{
                val jsonString = application.assets.open("json/profile.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val listPersonType = object : TypeToken<UserInfo>() {}.type
                val tempUserInfo: UserInfo = gson.fromJson(jsonString, listPersonType)
                userDao.insertUser(tempUserInfo.profile)

                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,email).commit()
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,password).commit()
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,tempUserInfo.profile.id).commit()
            }
            val isFingerPrintLoginEnabled = sharedPreferences.getBoolean(PreferenceKeys.APP_USER_LOGIN_FINGERPRINT_ENABLED,true)
            emit(
                DataState.data(
                    data = LoginViewState(loginFields = LoginFields("",email,password,isFingerPrintLoginEnabled)),
                    stateEvent = stateEvent,
                    response = null
                )
            )
        }


    }
    override fun setFingerPrintMode(checked: Boolean) {
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_USER_LOGIN_FINGERPRINT_ENABLED,checked).commit()
        sharedPrefsEditor.apply()
    }

    override fun checkFingerPrintEnableMode(stateEvent: StateEvent): Flow<DataState<LoginViewState>> = flow {
        val isFingerPrintLoginEnabled = sharedPreferences.getBoolean(PreferenceKeys.APP_USER_LOGIN_FINGERPRINT_ENABLED,true)
        emit(
            DataState.data(
                data = LoginViewState(isFingerPrintLoginEnabled = isFingerPrintLoginEnabled),
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