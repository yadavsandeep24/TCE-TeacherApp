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
import com.tce.teacherapp.util.*
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
            var isFingerPrintLoginEnabled = false
            var isFaceIdLoginEnabled = false
            val userId = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
            val isQuickAccessScreenShow = sharedPreferences.getBoolean(PreferenceKeys.APP_PREFERENCES_KEY_USER_QUICK_ACCESS_SCREEN_SHOW,true)

            if(userId?.isNotEmpty()!!) {
                val checkvalidUser =  userDao.getUserInfoData(email,password)
                if(checkvalidUser != null) {
                    sharedPrefsEditor.putString(
                        PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,
                        email
                    ).commit()
                    sharedPrefsEditor.putString(
                        PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,
                        password
                    ).commit()
                    sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, checkvalidUser.id).commit()
                    isFingerPrintLoginEnabled = checkvalidUser.fingerPrintMode
                    isFaceIdLoginEnabled = checkvalidUser.faceIdMode
                    emit(
                        DataState.data(
                            data = LoginViewState(loginFields = LoginFields("",email,password,isFingerPrintLoginEnabled,isFaceIdLoginEnabled,isQuickAccessScreenShow)),
                            stateEvent = stateEvent,
                            response = null
                        )
                    )
                }else{
                    emit(
                        DataState.error(
                            response = Response(
                                "Please enter valid Username or Password.",
                                UIComponentType.Dialog,
                                MessageType.Error(),
                                serviceTypes = RequestTypes.GENERIC
                            ),
                            stateEvent = stateEvent
                        )
                    )

                }
            }else{
                val jsonString = application.assets.open("json/profile.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val listPersonType = object : TypeToken<UserInfo>() {}.type
                val tempUserInfo: UserInfo = gson.fromJson(jsonString, listPersonType)
                tempUserInfo.profile.faceIdMode = isFaceIdLoginEnabled
                tempUserInfo.profile.fingerPrintMode = isFingerPrintLoginEnabled
                userDao.insertUser(tempUserInfo.profile)

                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,email).commit()
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,password).commit()
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,tempUserInfo.profile.id).commit()
                emit(
                    DataState.data(
                        data = LoginViewState(loginFields = LoginFields("",email,password,isFingerPrintLoginEnabled,isFaceIdLoginEnabled,isQuickAccessScreenShow)),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
            }
        }
    }
    override fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent): Flow<DataState<LoginViewState>> = flow {
        withContext((IO)) {
            val userId =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
            userDao.updateFingerPrintLoginMode(checked,userId!!)
            if(checked) {
                userDao.updateFaceIdLoginMode(false, userId)
            }else{
                userDao.updateFaceIdLoginMode(true, userId)
            }
        }
    }

    override fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> = flow {
        withContext((IO)) {
            val userId =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
            userDao.updateFaceIdLoginMode(checked,userId!!)
            if(checked) {
                userDao.updateFingerPrintLoginMode(false, userId)
            }else{
                userDao.updateFingerPrintLoginMode(true, userId)
            }
        }
    }

    override fun checkLoginMode(stateEvent: StateEvent): Flow<DataState<LoginViewState>>  = flow{
        withContext((IO)) {
            val userName =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,"")
            val userPassword =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,"")
            if (userName != null && userPassword != null) {
                    val userInfo = userDao.getUserInfoData(userName,userPassword)
                if (userInfo != null) {
                    emit(
                        DataState.data(
                            data = LoginViewState(isFingerPrintLoginEnabled = userInfo.fingerPrintMode,isFaceLoginEnabled = userInfo.faceIdMode),
                            stateEvent = stateEvent,
                            response = null
                        )
                    )
                }else{
                    emit(
                        DataState.data(
                            data = LoginViewState(isFingerPrintLoginEnabled = false,isFaceLoginEnabled = false),
                            stateEvent = stateEvent,
                            response = null
                        )
                    )
                }
            }else{
                emit(
                    DataState.data(
                        data = LoginViewState(isFingerPrintLoginEnabled = false,isFaceLoginEnabled = false),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
            }
        }
    }

    override fun getPreUserData(stateEvent: StateEvent): Flow<DataState<LoginViewState>>  = flow{
        withContext(IO){
            val userID = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
            val userInfo = userID?.let { userDao.getUserByUserId(it) }
            emit(
                DataState.data(
                    data = LoginViewState(profile = userInfo),
                    stateEvent = stateEvent,
                    response = null
                )
            )
        }

    }

    override fun setQuickAccessScreen(isShow: Boolean) {
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_KEY_USER_QUICK_ACCESS_SCREEN_SHOW, isShow).commit()
        sharedPrefsEditor.apply()
    }


    private fun resetSession(new: Boolean) {
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES,new).commit()
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS,new).commit()
    }
}