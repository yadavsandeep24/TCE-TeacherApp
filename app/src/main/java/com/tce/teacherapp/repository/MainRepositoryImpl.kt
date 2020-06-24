package com.tce.teacherapp.repository

import android.app.Application
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.api.response.BookResponse
import com.tce.teacherapp.api.response.GradeResponse
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.db.dao.UserDao
import com.tce.teacherapp.db.entity.*
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@FlowPreview
class MainRepositoryImpl
@Inject
constructor(
    val subjectDao: SubjectsDao,
    val userDao: UserDao,
    val tceService: TCEService,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor,
    val application: Application
) : MainRepository {
    private val TAG: String = "AppDebug"
    override fun getGrades(stateEvent: StateEvent): Flow<DataState<SubjectViewState>> = flow {

        val isNewSession =
            sharedPreferences.getBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES, true)
        if (isNewSession) {
            val apiResult = safeApiCall(IO) {
                tceService.getGrades()
            }
            emit(
                object : ApiResponseHandler<SubjectViewState, List<GradeResponse>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<GradeResponse>): DataState<SubjectViewState> {
                        val gradeList = toGradeList(resultObj)
                        withContext(IO) {
                            for (grade in gradeList) {
                                try {
                                    // Launch each insert as a separate job to be executed in parallel
                                    launch {
                                        Log.d(TAG, "updateLocalDb: inserting grade: $grade")
                                        subjectDao.insertGrade(grade)
                                        for (subject in grade.subjectList) {
                                            try {
                                                launch {
                                                    subjectDao.insert(subject)
                                                }
                                            } catch (e: Exception) {
                                                Log.e(
                                                    TAG,
                                                    "updateLocalDb: error updating cache data on subject  with title: ${subject.title}. " +
                                                            "${e.message}"
                                                )
                                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                                // Since there could be many blog posts being inserted/updated.
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                        "updateLocalDb: error updating cache data on blog post with gradeTitle: ${grade.gradeTitle}. " +
                                                "${e.message}"
                                    )
                                    // Could send an error report here or something but I don't think you should throw an error to the UI
                                    // Since there could be many blog posts being inserted/updated.
                                }
                            }
                        }
                        if (resultObj.isNotEmpty()) {
                            sharedPrefsEditor.putString(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_ID,
                                resultObj[0].id
                            ).commit()
                            sharedPrefsEditor.putInt(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION,
                                0
                            ).commit()
                            sharedPrefsEditor.putBoolean(
                                PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES,
                                false
                            ).commit()
                        }
                        val viewState = SubjectViewState(gradeList = gradeList)
                        return DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )

                    }
                }.getResult()
            )
        } else {
            val apiResult = safeCacheCall(IO) {
                val selectedGrade =
                    sharedPreferences.getString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, "")
                selectedGrade?.let {
                    subjectDao.getGradeListData()
                }
            }
            emit(
                object : CacheResponseHandler<SubjectViewState, List<Grade>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<Grade>): DataState<SubjectViewState> {
                        if (resultObj.isNotEmpty()) {
                            sharedPrefsEditor.putString(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_ID,
                                resultObj[0].id
                            ).commit()
                            sharedPrefsEditor.putInt(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION,
                                0
                            ).commit()
                            sharedPrefsEditor.putBoolean(
                                PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES,
                                false
                            ).commit()
                        }
                        return DataState.data(
                            data = SubjectViewState(gradeList = resultObj),
                            response = null,
                            stateEvent = stateEvent
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun getSubjects(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {
        val apiResult = safeCacheCall(IO) {
            val selectedGrade =
                sharedPreferences.getString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, "")
            selectedGrade?.let {
                if (query.isEmpty()) {
                    subjectDao.getSubjectListData(it)
                } else {
                    subjectDao.getSubjectListData(query, it)
                }
            }
        }
        emit(
            object : CacheResponseHandler<SubjectViewState, List<Subject>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<Subject>): DataState<SubjectViewState> {
                    return DataState.data(
                        data = SubjectViewState(
                            subjectList = resultObj,
                            selectedGradePosition = sharedPreferences.getInt(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION,
                                0
                            )
                        ),
                        response = null,
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }

    override fun setSelectedGrade(
        grade: Grade,
        position: Int,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {
        sharedPrefsEditor.putString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, grade.id).commit()
        sharedPrefsEditor.putInt(PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION, position).commit()
        val apiResult = safeCacheCall(IO) {
            val selectedGrade =
                sharedPreferences.getString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID, "")
            selectedGrade?.let {
                subjectDao.getSubjectListData(it)
            }
        }
        emit(
            object : CacheResponseHandler<SubjectViewState, List<Subject>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<Subject>): DataState<SubjectViewState> {
                    return DataState.data(
                        data = SubjectViewState(
                            subjectList = resultObj,
                            selectedGradePosition = sharedPreferences.getInt(
                                PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION,
                                0
                            )
                        ),
                        response = null,
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )
    }

    override fun getTopicList(
        query: String,
        bookID: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {
        val isNewSession =
            sharedPreferences.getBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS, true)
        if (isNewSession) {
            val apiResult = safeApiCall(IO) {
                tceService.getBooks()
            }
            emit(
                object : ApiResponseHandler<SubjectViewState, List<BookResponse>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(networkObject: List<BookResponse>): DataState<SubjectViewState> {
                        val bookList = toBookList(networkObject)
                        withContext(IO) {
                            for (book in bookList) {
                                try {
                                    // Launch each insert as a separate job to be executed in parallel
                                    launch {
                                        Log.d(TAG, "updateLocalDb: inserting book: $book")
                                        subjectDao.insertBook(book)
                                        for (node in book.node) {
                                            try {
                                                subjectDao.insertNode(node)
                                                for (nodex in node.node) {
                                                    try {
                                                        subjectDao.inserNodeX(nodex)
                                                        for (nodexx in nodex.node) {
                                                            try {
                                                                subjectDao.insertNodeXX(nodexx)
                                                                for (nodexxx in nodexx.node) {
                                                                    try {
                                                                        subjectDao.insertNodeXXX(
                                                                            nodexxx
                                                                        )
                                                                    } catch (e: Exception) {
                                                                    }
                                                                }
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        var topicList = subjectDao.getTopicListData(bookID)
                        if (query.isNotEmpty()) {
                            topicList = subjectDao.getTopicListData(query, bookID)
                        }
                        sharedPrefsEditor.putBoolean(
                            PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS,
                            false
                        ).commit()
                        val viewState = SubjectViewState(
                            topicList = topicList
                        )
                        return DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )
                    }


                }.getResult()
            )
        } else {
            val apiResult = safeCacheCall(IO) {
                if (query.isEmpty()) {
                    subjectDao.getTopicListData(bookID)
                } else {
                    subjectDao.getTopicListData(query, bookID)
                }
            }
            emit(
                object : CacheResponseHandler<SubjectViewState, List<Node>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<Node>): DataState<SubjectViewState> {
                        sharedPrefsEditor.putBoolean(
                            PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS,
                            false
                        ).commit()
                        val viewState = SubjectViewState(
                            topicList = resultObj
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

    override fun getMessage(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> = flow {

        withContext(IO) {
            var jsonString: String = ""
            try {

                jsonString = application.assets.open("json/messaage.json").bufferedReader()
                    .use { it.readText() }
                val gson = Gson()
                val listPersonType = object : TypeToken<List<Message>>() {}.type
                val messageList: List<Message> = gson.fromJson(jsonString, listPersonType)

                val selectedList: List<Message>;
                if (TextUtils.isEmpty(query)) {
                    selectedList = messageList
                } else {
                    selectedList =
                        messageList.filter { it.title.contains(query, ignoreCase = true) }
                }

                emit(
                    DataState.data(
                        data = MessageViewState(messageList = selectedList),
                        stateEvent = stateEvent,
                        response = null
                    )
                )

            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        }

    }

    override fun getStudentList(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> = flow {
        var jsonString: String = ""
        try {

            jsonString = application.assets.open("json/student.json").bufferedReader()
                .use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<List<Student>>() {}.type
            var studentList: List<Student> = gson.fromJson(jsonString, listPersonType)

            var selectedList: List<Student>;
            if (TextUtils.isEmpty(query)) {
                selectedList = studentList
            } else {
                selectedList = studentList.filter { it.name.contains(query, ignoreCase = true) }
            }

            emit(
                DataState.data(
                    data = MessageViewState(studentList = selectedList),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

    }

    override fun getResourceList(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> = flow {
        var jsonString: String = ""
        try {

            jsonString = application.assets.open("json/resourceType.json").bufferedReader()
                .use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<List<MessageResource>>() {}.type
            var resourceList: List<MessageResource> = gson.fromJson(jsonString, listPersonType)

            var selectedList: List<MessageResource>;
            if (TextUtils.isEmpty(query)) {
                selectedList = resourceList
            } else {
                selectedList = resourceList.filter { it.title.contains(query, ignoreCase = true) }
            }

            emit(
                DataState.data(
                    data = MessageViewState(resourceList = selectedList),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }


    }

    override fun getSelectedResourceList(
        query: String,
        typeId: Int,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>> = flow {

        var jsonString: String = ""
        try {

            jsonString =
                application.assets.open("json/resource.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<List<MessageResource>>() {}.type
            var resourceList: List<MessageResource> = gson.fromJson(jsonString, listPersonType)

            var selectedList: List<MessageResource>;

            if (TextUtils.isEmpty(query)) {
                selectedList = resourceList.filter { it.typeId == typeId }
            } else {
                selectedList = resourceList.filter {
                    it.title.contains(
                        query,
                        ignoreCase = true
                    ) && it.typeId == typeId
                }
            }

            emit(
                DataState.data(
                    data = MessageViewState(selectedResourceList = selectedList),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }


    }

    override fun getProfile(stateEvent: StateEvent): Flow<DataState<DashboardViewState>> = flow {
            withContext(IO){
                    val userID = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
                    val userInfo = userID?.let { userDao.getUserByUserId(it) }
                emit(
                    DataState.data(
                        data = DashboardViewState(profile = userInfo),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
            }
    }

    override fun updateProfilePic(
        resultUri: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>  = flow{
        withContext((IO)) {
            val userId =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
            userDao.updateProfilePic(resultUri,userId!!)
        }
    }

    override fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent): Flow<DataState<DashboardViewState>> = flow {
        withContext((IO)) {
            val userId =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
            userDao.updateFingerPrintLoginMode(checked,userId!!)
            userDao.updateFaceIdLoginMode(!checked, userId)
            emit(
                DataState.data(
                    data = DashboardViewState(isFingerPrintLoginEnabled = checked,isFaceLoginEnabled = !checked),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        }
    }

    override fun getDashboardData(
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>  = flow {

        withContext(IO) {}
        val gson = Gson()

        try {
                var jsonString = application.assets.open("json/event.json").bufferedReader()
                    .use { it.readText() }
                var listType = object : TypeToken<ArrayList<Event>>() {}.type
                val eventList: ArrayList<Event> = gson.fromJson(jsonString, listType)
                val selectedEventList: ArrayList<Event> = ArrayList();
                for (i in 0 until 3) {
                    selectedEventList.add(eventList[i])
                }
            jsonString = application.assets.open("json/dashboardResource.json").bufferedReader()
                .use { it.readText() }
            listType = object : TypeToken<ArrayList<DashboardResource>>() {}.type
            val resourceList: ArrayList<DashboardResource> = gson.fromJson(jsonString, listType)
            val selectedTodayResourceList: ArrayList<DashboardResource> = ArrayList();
            for (i in 0 until 2) {
                selectedTodayResourceList.add(resourceList[i])
            }


            jsonString =
                application.assets.open("json/dashboardResourceType.json").bufferedReader()
                    .use { it.readText() }
            listType = object : TypeToken<ArrayList<DashboardResourceType>>() {}.type
            val lastViewedResourceList: ArrayList<DashboardResourceType> = gson.fromJson(jsonString, listType)
            val selectedLastViewedResourceList: ArrayList<DashboardResourceType> = ArrayList();
            for (i in 0 until 4) {
                selectedLastViewedResourceList.add(lastViewedResourceList[i])
            }
                emit(
                    DataState.data(
                        data = DashboardViewState(eventList = selectedEventList,todayResourceList = selectedTodayResourceList,lastViewedResourceList = selectedLastViewedResourceList),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

    }

    override fun getEventList(count : Int,stateEvent: StateEvent): Flow<DataState<DashboardViewState>> =
        flow {
        var jsonString: String = ""
        try {

            jsonString =
                application.assets.open("json/event.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<ArrayList<Event>>() {}.type
            var eventList: ArrayList<Event> = gson.fromJson(jsonString, listPersonType)

            var selectedList: ArrayList<Event> = ArrayList();

            for (i in 0 until count) {
                selectedList.add(eventList.get(i))
            }

            emit(
                DataState.data(
                    data = DashboardViewState(eventList = selectedList),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }


    }

    override fun getTodayResourceList(
        count: Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        var jsonString: String = ""
        try {

            jsonString =
                application.assets.open("json/dashboardResource.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<ArrayList<DashboardResource>>() {}.type
            var eventList: ArrayList<DashboardResource> = gson.fromJson(jsonString, listPersonType)

            var selectedList: ArrayList<DashboardResource> = ArrayList();

            for (i in 0 until count) {
                selectedList.add(eventList.get(i))
            }

            emit(
                DataState.data(
                    data = DashboardViewState(todayResourceList = selectedList),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }


    }

    override fun getLastViewedResourceList(
        count: Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        var jsonString: String = ""
        try {
            jsonString =
                application.assets.open("json/dashboardResourceType.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<ArrayList<DashboardResourceType>>() {}.type
            var eventList: ArrayList<DashboardResourceType> = gson.fromJson(jsonString, listPersonType)

            var selectedList: ArrayList<DashboardResourceType> = ArrayList();

            for (i in 0 until count) {
                selectedList.add(eventList.get(i))
            }

            emit(
                DataState.data(
                    data = DashboardViewState(lastViewedResourceList = selectedList),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

    }


    override fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        withContext((IO)) {
            val userId =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")
            userDao.updateFaceIdLoginMode(checked,userId!!)
            userDao.updateFingerPrintLoginMode(!checked, userId)

            emit(
                DataState.data(
                    data = DashboardViewState(isFingerPrintLoginEnabled = !checked,isFaceLoginEnabled = checked),
                    stateEvent = stateEvent,
                    response = null
                )
            )
        }
    }

    override fun checkLoginMode(stateEvent: StateEvent): Flow<DataState<DashboardViewState>>  = flow{
        withContext((IO)) {
            val userName =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL,"")
            val userPassword =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,"")

            Log.d("SAN", "userName-->$userName/userPassword-->$userPassword")
            if (userName != null && userPassword != null) {
                val userInfo = userDao.getUserInfoData(userName,userPassword)
                if (userInfo != null) {
                    Log.d("SAN", "fingerPrintMode-->${userInfo.fingerPrintMode}")
                    emit(
                        DataState.data(
                            data = DashboardViewState(isFingerPrintLoginEnabled = userInfo.fingerPrintMode,isFaceLoginEnabled = userInfo.faceIdMode),
                            stateEvent = stateEvent,
                            response = null
                        )
                    )
                }
            }
        }
    }

    override fun updatePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow{
        withContext((IO)) {
            val userId =  sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID,"")

            val dbOldPassword = userId?.let { userDao.getOldPassword(it) }
            if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                emit(
                    DataState.error(
                        response = Response(
                            MessageConstant.LOGIN_MANDATORY_FIELD,
                            UIComponentType.Dialog,
                            MessageType.Error(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                )
            }else if(!newPassword.equals(confirmPassword,false)) {
                emit(
                    DataState.error(
                        response = Response(
                            MessageConstant.UPDATE_PASSWORD_NEW_CONFIRM_MISMATCH,
                            UIComponentType.Dialog,
                            MessageType.Error(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                )
            }else if(oldPassword.isNotEmpty() && !dbOldPassword.equals(oldPassword,false))
            {
                emit(
                     DataState.error(
                        response = Response(
                            "Old Password mismatch.",
                            UIComponentType.Dialog,
                            MessageType.Error(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                )
            }else {
                userDao.updatePassword(newPassword, userId!!)
                sharedPrefsEditor.putString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,newPassword).commit()
                emit(
                    DataState.error(
                        response = Response(
                            "Password changed successfully.",
                            UIComponentType.Dialog,
                            MessageType.Success(),
                            serviceTypes = RequestTypes.GENERIC
                        ),
                        stateEvent = stateEvent
                    )
                )
            }
        }
    }

    override fun updateProfile(
        userInfo: Profile,
        stateMessage: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {

        withContext((IO)) {
            val tempUserInfo = userDao.getUserByUserId(userInfo.id)
            if (tempUserInfo != null) {
                userInfo.password =  tempUserInfo.password
                userInfo.fingerPrintMode = tempUserInfo.fingerPrintMode
                userInfo.imageUrl = tempUserInfo.imageUrl
                userInfo.faceIdMode = tempUserInfo.faceIdMode
            }
            userDao.insertUser(userInfo)
            emit(
                DataState.error(
                    response = Response(
                        "Profile updated successfully.",
                        UIComponentType.Dialog,
                        MessageType.Success(),
                        serviceTypes = RequestTypes.GENERIC
                    ),
                    stateEvent = stateMessage
                )
            )
        }
    }

    fun toGradeList(grades: List<GradeResponse>): List<Grade> {
        val gradeList: ArrayList<Grade> = ArrayList()
        for (gradeResponse in grades) {
            val grade = gradeResponse.toGrade()
            gradeList.add(
                gradeResponse.toGrade()
            )
        }
        return gradeList
    }

    fun toBookList(books: List<BookResponse>): List<Book> {
        val bookList: ArrayList<Book> = ArrayList()
        for (bookResponse in books) {
            bookList.add(
                bookResponse.toBook()
            )
        }
        return bookList
    }



}





