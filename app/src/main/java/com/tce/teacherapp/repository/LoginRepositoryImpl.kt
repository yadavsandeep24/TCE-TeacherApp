package com.tce.teacherapp.repository

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.api.response.tceapi.*
import com.tce.teacherapp.db.dao.UserDao
import com.tce.teacherapp.db.entity.UserInfo
import com.tce.teacherapp.ui.login.state.LoginFields
import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.ui.login.state.LoginViewState
import com.tce.teacherapp.ui.login.state.RegisterUserFields
import com.tce.teacherapp.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject


@FlowPreview
class LoginRepositoryImpl
@Inject
constructor(
    val userDao: UserDao,
    val tceService: TCEService,
    val zlService: TCEService,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor,
    val application: Application
) : LoginRepository {
    override fun getAPISessionTimeOut(): String {
        return sharedPreferences.getString(PreferenceKeys.APP_API_SESSION_TIMEOUT, "300").toString()
    }

    override fun attemptLogin(
        schoolName: String?,
        email: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> = flow {
        resetSession(true)

        withContext(IO) {
            val loginFieldErrors = LoginFields(
                login_email = email,
                login_password = password,
                login_schoolName = schoolName
            ).isValidForLogin()
            if (loginFieldErrors == LoginFields.LoginError.none()) {
                var isFingerPrintLoginEnabled = false
                var isFaceIdLoginEnabled = false
                val userId =
                    sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
                val isQuickAccessScreenShow = sharedPreferences.getBoolean(
                    PreferenceKeys.APP_PREFERENCES_KEY_USER_QUICK_ACCESS_SCREEN_SHOW,
                    true
                )

                if (userId?.isNotEmpty()!!) {
                    val checkvalidUser = userDao.getUserInfoData(email, password)
                    if (checkvalidUser != null) {
                        sharedPrefsEditor.putString(
                            PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,
                            email
                        ).commit()
                        sharedPrefsEditor.putString(
                            PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,
                            password
                        ).commit()
                        sharedPrefsEditor.putString(
                            PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,
                            checkvalidUser.id
                        ).commit()
                        isFingerPrintLoginEnabled = checkvalidUser.fingerPrintMode
                        isFaceIdLoginEnabled = checkvalidUser.faceIdMode
                        emit(
                            DataState.data(
                                data = LoginViewState(
                                    loginFields = LoginFields(
                                        "",
                                        email,
                                        password,
                                        isFingerPrintLoginEnabled,
                                        isFaceIdLoginEnabled,
                                        isQuickAccessScreenShow
                                    )
                                ),
                                stateEvent = stateEvent,
                                response = null
                            )
                        )
                    } else {
                        emit(
                            DataState.error<LoginViewState>(
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
                } else {
                    val jsonString = application.assets.open("json/profile.json").bufferedReader()
                        .use { it.readText() }
                    val gson = Gson()
                    val listPersonType = object : TypeToken<UserInfo>() {}.type
                    val tempUserInfo: UserInfo = gson.fromJson(jsonString, listPersonType)
                    tempUserInfo.profile.faceIdMode = isFaceIdLoginEnabled
                    tempUserInfo.profile.fingerPrintMode = isFingerPrintLoginEnabled
                    userDao.insertUser(tempUserInfo.profile)

                    sharedPrefsEditor.putString(
                        PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,
                        email
                    ).commit()
                    sharedPrefsEditor.putString(
                        PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,
                        password
                    ).commit()
                    sharedPrefsEditor.putString(
                        PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,
                        tempUserInfo.profile.id
                    ).commit()
                    emit(
                        DataState.data(
                            data = LoginViewState(
                                loginFields = LoginFields(
                                    "",
                                    email,
                                    password,
                                    isFingerPrintLoginEnabled,
                                    isFaceIdLoginEnabled,
                                    isQuickAccessScreenShow
                                )
                            ),
                            stateEvent = stateEvent,
                            response = null
                        )
                    )
                }
            } else {
                Log.d("SAN", "emitting error: $loginFieldErrors")
                emit(
                    DataState.error<LoginViewState>(
                        response = Response(
                            message = loginFieldErrors,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )

                )
            }

        }
    }

    override fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> = flow {
        withContext((IO)) {
            val userId = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            userDao.updateFingerPrintLoginMode(checked, userId!!)
            if (checked) {
                userDao.updateFaceIdLoginMode(false, userId)
            } else {
                userDao.updateFaceIdLoginMode(true, userId)
            }
        }
    }

    override fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> = flow {
        withContext((IO)) {
            val userId = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            userDao.updateFaceIdLoginMode(checked, userId!!)
            if (checked) {
                userDao.updateFingerPrintLoginMode(false, userId)
            } else {
                userDao.updateFingerPrintLoginMode(true, userId)
            }
        }
    }

    override fun checkLoginMode(stateEvent: StateEvent): Flow<DataState<LoginViewState>> = flow {
        withContext((IO)) {
            val userName =
                sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL, "")
            val userPassword =
                sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD, "")
            if (userName != null && userPassword != null) {
                val userInfo = userDao.getUserInfoData(userName, userPassword)
                if (userInfo != null) {
                    emit(
                        DataState.data(
                            data = LoginViewState(
                                isFingerPrintLoginEnabled = userInfo.fingerPrintMode,
                                isFaceLoginEnabled = userInfo.faceIdMode
                            ),
                            stateEvent = stateEvent,
                            response = null
                        )
                    )
                } else {
                    emit(
                        DataState.data(
                            data = LoginViewState(
                                isFingerPrintLoginEnabled = false,
                                isFaceLoginEnabled = false
                            ),
                            stateEvent = stateEvent,
                            response = null
                        )
                    )
                }
            } else {
                emit(
                    DataState.data(
                        data = LoginViewState(
                            isFingerPrintLoginEnabled = false,
                            isFaceLoginEnabled = false
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
            }
        }
    }

    override fun getPreUserData(stateEvent: StateEvent): Flow<DataState<LoginViewState>> = flow {
        withContext(IO) {
            val userID = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
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
        sharedPrefsEditor.putBoolean(
            PreferenceKeys.APP_PREFERENCES_KEY_USER_QUICK_ACCESS_SCREEN_SHOW,
            isShow
        ).commit()
        sharedPrefsEditor.apply()
    }

    override fun resentOTP(
        strMobileNo: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> = flow {

        emit(
            DataState.error(
                response = Response(
                    "OTP has been sent.",
                    UIComponentType.Dialog,
                    MessageType.Error(),
                    serviceTypes = RequestTypes.GENERIC
                ),
                stateEvent = stateEvent
            )
        )
    }

    override fun registerUser(
        mobileNo: String,
        password: String,
        stateEvent: StateEvent
    ): Flow<DataState<LoginViewState>> = flow {
        withContext(IO) {
            val registerFieldErrors = RegisterUserFields(
                mobile_no = mobileNo,
                password = password
            ).checkValidRegistration()
            if (registerFieldErrors == LoginFields.LoginError.none()) {

                emit(
                    DataState.data(
                        data = LoginViewState(
                            registerFields = RegisterUserFields(
                                mobileNo,
                                password
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
            } else {
                Log.d("SAN", "emitting error: $registerFieldErrors")
                emit(
                    DataState.error<LoginViewState>(
                        response = Response(
                            message = registerFieldErrors,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                )
            }
        }

    }

    override fun getSchoolList(
        schoolName: String,
        stateEvent: LoginStateEvent
    ): Flow<DataState<LoginViewState>> = flow {
        withContext(IO) {
            val apiResult = safeApiCall(IO) {
                tceService.getSchoolLists("1", schoolName)
            }
            emit(
                object : ApiResponseHandler<LoginViewState, SchoolListResponse>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: SchoolListResponse): DataState<LoginViewState> {

                        val viewState = LoginViewState(
                            SchoolResponse = resultObj.suggestions
                        )
                        return DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun getClientID(stateEvent: LoginStateEvent): Flow<DataState<LoginViewState>> = flow {
        withContext(IO) {
            val apiClientIDResult = safeApiCall(IO) {
                tceService.getClientId()
            }


            emit(
                object : ApiResponseHandler<LoginViewState, ClientIDResponse>(
                    response = apiClientIDResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: ClientIDResponse): DataState<LoginViewState> {
                        sharedPrefsEditor.putString(
                            PreferenceKeys.APP_API_VERSION,
                            resultObj.apiVersion
                        ).commit()
                        sharedPrefsEditor.putString(
                            PreferenceKeys.APP_API_SESSION_TIMEOUT,
                            resultObj.sessionTimeout
                        ).commit()
                        val viewState = LoginViewState(
                            clientIdRes = resultObj
                        )
                        return DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )
                    }
                }.getResult()
            )

        }
    }

    override fun tceLogin(
        schoolName: String,
        email: String,
        password: String,
        stateEvent: LoginStateEvent
    ): Flow<DataState<LoginViewState>> = flow {
        withContext(IO) {
            val tceLoginResult = safeApiCall(IO) {

                val username = "$schoolName#$email"
                val grant_type = "password"

                val userNameReq: RequestBody =
                    RequestBody.create(MediaType.parse("text/plain"), username)
                val passwordReq: RequestBody =
                    RequestBody.create(MediaType.parse("text/plain"), password)
                val granttypeReq: RequestBody =
                    RequestBody.create(MediaType.parse("text/plain"), grant_type)
                tceService.getAccessToken("1", userNameReq, passwordReq, granttypeReq)
            }

            emit(
                object : ApiResponseHandler<LoginViewState, LoginResponse>(
                    response = tceLoginResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: LoginResponse): DataState<LoginViewState> {
                        Log.d("SAN", "resultObj.access_token-->" + resultObj.access_token)
                        sharedPrefsEditor.putString(
                            PreferenceKeys.APP_USER_ACCESSTOKEN,
                            resultObj.access_token
                        ).commit()
                        sharedPrefsEditor.putString(
                            PreferenceKeys.APP_USER_REFRESHTOKEN,
                            resultObj.refresh_token
                        ).commit()
                        return DataState.data(
                            data = LoginViewState(
                                loginFields = LoginFields(
                                    email,
                                    password,
                                    schoolName,
                                    login_mode_fingePrint_Enabled = false,
                                    login_mode_faceId_Enabled = false,
                                    isQuickAccessScreenShow = false
                                )
                            ),
                            stateEvent = stateEvent,
                            response = null
                        )
                    }
                }.getResult()
            )
        }

    }

    override fun extendToken(): Boolean  {
        var isReturn = false
                val accessToken =
                    sharedPreferences.getString(PreferenceKeys.APP_USER_ACCESSTOKEN, "")
                val refreshToken =
                    sharedPreferences.getString(PreferenceKeys.APP_USER_REFRESHTOKEN, "")

                tceService.extendToken("1", accessToken.toString())
                    .enqueue(object : Callback<ExtendTokenResponse> {
                        override fun onFailure(call: Call<ExtendTokenResponse>, t: Throwable) {
                            isReturn = false
                        }

                        override fun onResponse(
                            call: Call<ExtendTokenResponse>,
                            response: retrofit2.Response<ExtendTokenResponse>
                        ) {
                            Log.d(
                                "SAN",
                                "response.isSuccessful-->" + response.isSuccessful + "/response.code()-->" + response.code()
                            )
                            if (response.isSuccessful && response.code() == 200 && response.body() != null && response.body()?.status != null) {
                              isReturn = true
                            } else if (response.code() == 401) {
                                val acceessTokenBody: RequestBody =
                                    RequestBody.create(MediaType.parse("text/plain"), accessToken)
                                val refreshTokenBody: RequestBody =
                                    RequestBody.create(MediaType.parse("text/plain"), refreshToken)
                                val granttypeReq: RequestBody =
                                    RequestBody.create(
                                        MediaType.parse("text/plain"),
                                        "refresh_token"
                                    )
                                tceService.getRefreshToken(
                                    "1",
                                    refreshTokenBody,
                                    acceessTokenBody,
                                    granttypeReq
                                ).enqueue(object : Callback<RefreshTokenResponse> {
                                    override fun onFailure(
                                        call: Call<RefreshTokenResponse>,
                                        t: Throwable
                                    ) {
                                        isReturn = true
                                    }

                                    override fun onResponse(
                                        call: Call<RefreshTokenResponse>,
                                        response: retrofit2.Response<RefreshTokenResponse>
                                    ) {
                                        if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                                            sharedPrefsEditor.putString(
                                                PreferenceKeys.APP_USER_ACCESSTOKEN,
                                                response.body()!!.access_token
                                            ).commit()
                                            sharedPrefsEditor.putString(
                                                PreferenceKeys.APP_USER_REFRESHTOKEN,
                                                response.body()!!.refresh_token
                                            ).commit()
                                            isReturn = true
                                        } else {
                                            isReturn = false

                                        }
                                    }

                                })
                            }
                        }

                    })

        return isReturn


    }


    private fun resetSession(new: Boolean) {
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES, new)
            .commit()
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS, new).commit()
        sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_RESOURCES, new)
            .commit()
    }
}