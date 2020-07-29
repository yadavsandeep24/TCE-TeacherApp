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
import com.tce.teacherapp.api.response.ResourceListResponse
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.db.dao.UserDao
import com.tce.teacherapp.db.entity.*
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.home.state.UpdatePasswordFields
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.ui.login.state.LoginFields
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
    override fun getTCEApplication(): Application {
        return  application
    }

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
                                                Log.e(TAG,"updateLocalDb: ${subject.title}. " +"${e.message}")
                                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                                // Since there could be many blog posts being inserted/updated.
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG,"updateLocalDb: ${grade.gradeTitle}. " + "${e.message}")
                                    // Could send an error report here or something but I don't think you should throw an error to the UI
                                    // Since there could be many blog posts being inserted/updated.
                                }
                            }
                        }
                        if (resultObj.isNotEmpty()) {
                            sharedPrefsEditor.putString(PreferenceKeys.APP_USER_SELECTED_GRADE_ID,resultObj[0].id).commit()
                            sharedPrefsEditor.putInt(PreferenceKeys.APP_USER_SELECTED_GRADE_POSITION,0).commit()
                            sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_GRADES,false).commit()
                        }
                        val viewState = SubjectViewState(gradeList = gradeList)
                        return DataState.data(response = null, data = viewState, stateEvent = stateEvent)

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
                                        for (topic in book.topicList) {
                                            try {
                                                subjectDao.insertTopic(topic)
                                                for (chapter in topic.chapterList) {
                                                    try {
                                                        subjectDao.insertChapter(chapter)
                                                        for (chapterResourceType in chapter.chapterResourceTypeList) {
                                                            try {
                                                                subjectDao.insertChapterResourceType(
                                                                    chapterResourceType
                                                                )
                                                                for (resource in chapterResourceType.resourceList) {
                                                                    try {
                                                                        subjectDao.insertResource(
                                                                            resource
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
                        val viewState = SubjectViewState(topicList = topicList)
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
                object : CacheResponseHandler<SubjectViewState, List<Topic>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<Topic>): DataState<SubjectViewState> {
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
        var jsonString = ""
        try {

            jsonString = application.assets.open("json/student.json").bufferedReader()
                .use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<List<Student>>() {}.type
            val studentList: List<Student> = gson.fromJson(jsonString, listPersonType)

            val selectedList: List<Student>;
            selectedList = if (TextUtils.isEmpty(query)) {
                studentList
            } else {
                studentList.filter { it.name.contains(query, ignoreCase = true) }
            }

            emit(DataState.data(data = MessageViewState(studentList = selectedList),stateEvent = stateEvent,response = null))

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

    }

    override fun getResourceList(query: String,stateEvent: StateEvent): Flow<DataState<MessageViewState>> = flow {
        val jsonString: String
        try {

            jsonString = application.assets.open("json/resourceType.json").bufferedReader()
                .use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<List<MessageResource>>() {}.type
            val resourceList: List<MessageResource> = gson.fromJson(jsonString, listPersonType)

            val selectedList: List<MessageResource>;
            selectedList = if (TextUtils.isEmpty(query)) {
                resourceList
            } else {
                resourceList.filter { it.title.contains(query, ignoreCase = true) }
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

        val jsonString: String
        try {
            jsonString =
                application.assets.open("json/resource.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            val listPersonType = object : TypeToken<List<MessageResource>>() {}.type
            val resourceList: List<MessageResource> = gson.fromJson(jsonString, listPersonType)

            val selectedList: List<MessageResource>;

            selectedList = if (TextUtils.isEmpty(query)) {
                resourceList.filter { it.typeId == typeId }
            } else {
                resourceList.filter {
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
        withContext(IO) {
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
    ): Flow<DataState<DashboardViewState>> = flow {
        withContext((IO)) {
            val userId = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            userDao.updateProfilePic(resultUri, userId!!)
        }
    }

    override fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        withContext((IO)) {
            val userId = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            userDao.updateFingerPrintLoginMode(checked, userId!!)
            emit(
                DataState.data(
                    data = DashboardViewState(isFingerPrintLoginEnabled = checked),
                    stateEvent = stateEvent,
                    response = null
                )
            )
        }
    }

    override fun getTeacherDashboardData(
        id: Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        Log.d("SAN", "id-->$id")

        withContext(IO) {
            val userID = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            val userInfo = userID?.let { userDao.getUserByUserId(it) }
            var tempGSON: Gson

            try {
                var jsonString: String = if (id > 1) {
                    application.assets.open("json/event1.json").bufferedReader()
                        .use { it.readText() }
                } else {
                    application.assets.open("json/event.json").bufferedReader()
                        .use { it.readText() }
                }
                tempGSON = Gson()
                var listType = object : TypeToken<ArrayList<Event>>() {}.type
                val eventList: ArrayList<Event> = tempGSON.fromJson(jsonString, listType)
                val selectedEventList: ArrayList<Event> = ArrayList()

                val isShowLess = false
                var nextEventCount = 4
                if (eventList.size > 3) {
                    for (i in 0 until 3) {
                        selectedEventList.add(eventList[i])
                    }
                    if (eventList.size < 7) {
                        nextEventCount = eventList.size - 3
                    }
                } else {
                    for (i in 0 until eventList.size) {
                        selectedEventList.add(eventList[i])
                    }
                    nextEventCount = 0
                }
                val eventData = EventData(isShowLess, nextEventCount, selectedEventList)


                jsonString = if (id > 1) {
                    application.assets.open("json/todaysResource1.json").bufferedReader()
                        .use { it.readText() }
                } else {
                    application.assets.open("json/todaysResource.json").bufferedReader()
                        .use { it.readText() }
                }
                tempGSON = Gson()
                listType = object : TypeToken<ArrayList<DashboardResource>>() {}.type
                val resourceList: ArrayList<DashboardResource> =
                    tempGSON.fromJson(jsonString, listType)
                val selectedTodayResourceList: ArrayList<DashboardResource> = ArrayList()

                if (resourceList.size > 2) {
                    for (i in 0 until 2) {
                        selectedTodayResourceList.add(resourceList[i])
                    }
                    if (resourceList.size < 8) {
                        nextEventCount = resourceList.size - 2
                    }
                } else {
                    for (i in 0 until resourceList.size) {
                        selectedTodayResourceList.add(resourceList[i])
                    }
                    nextEventCount = 0
                }

                val todayResourceData =
                    TodaysResourceData(isShowLess, nextEventCount, selectedTodayResourceList)

                jsonString = if (id > 1) {
                    application.assets.open("json/lastViewedResource1.json").bufferedReader()
                        .use { it.readText() }
                } else {
                    application.assets.open("json/lastViewedResource.json").bufferedReader()
                        .use { it.readText() }
                }
                tempGSON = Gson()
                listType = object : TypeToken<ArrayList<DashboardResourceType>>() {}.type
                val lastViewedResourceList: ArrayList<DashboardResourceType> =
                    tempGSON.fromJson(jsonString, listType)
                val selectedLastViewedResourceList: ArrayList<DashboardResourceType> = ArrayList()

                if (lastViewedResourceList.size > 2) {
                    for (i in 0 until 2) {
                        selectedLastViewedResourceList.add(lastViewedResourceList[i])
                    }
                    if (lastViewedResourceList.size < 12) {
                        nextEventCount = lastViewedResourceList.size - 2
                    }
                } else {
                    for (i in 0 until lastViewedResourceList.size) {
                        selectedLastViewedResourceList.add(lastViewedResourceList[i])
                    }
                    nextEventCount = 0
                }

                val lastViewResourceData =
                    LastViewResourceData(isShowLess, nextEventCount, selectedLastViewedResourceList)

                jsonString = application.assets.open("json/class.json").bufferedReader()
                    .use { it.readText() }
                tempGSON = Gson()
                val listClass = object : TypeToken<List<ClassListsItem>>() {}.type
                val userClassList: List<ClassListsItem> = tempGSON.fromJson(jsonString, listClass)

                emit(
                    DataState.data(
                        data = DashboardViewState(
                            profile = userInfo,
                            eventData = eventData,
                            todayResourceData = todayResourceData,
                            lastViewedResourceData = lastViewResourceData,
                            classList = userClassList
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        }

    }

    override fun getParentDashboardData(
        id: Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        Log.d("SAN", "id-->$id")

        withContext(IO) {
            val userID = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            val userInfo = userID?.let { userDao.getUserByUserId(it) }
            var tempGSON: Gson

            try {
                var jsonString: String = if (id > 1) {
                    application.assets.open("json/event1.json").bufferedReader()
                        .use { it.readText() }
                } else {
                    application.assets.open("json/event.json").bufferedReader()
                        .use { it.readText() }
                }
                tempGSON = Gson()
                val listType = object : TypeToken<ArrayList<Event>>() {}.type
                val eventList: ArrayList<Event> = tempGSON.fromJson(jsonString, listType)
                val selectedEventList: ArrayList<Event> = ArrayList()

                val isShowLess = false
                var nextEventCount = 4
                if (eventList.size > 3) {
                    for (i in 0 until 3) {
                        selectedEventList.add(eventList[i])
                    }
                    if (eventList.size < 7) {
                        nextEventCount = eventList.size - 3
                    }
                } else {
                    for (i in 0 until eventList.size) {
                        selectedEventList.add(eventList[i])
                    }
                    nextEventCount = 0
                }
                val eventData = EventData(isShowLess, nextEventCount, selectedEventList)


                val jsonParentUpdateString: String = if (id > 1) {
                    application.assets.open("json/parentLatestUpdate1.json").bufferedReader()
                        .use { it.readText() }
                } else {
                    application.assets.open("json/parentLatestUpdate.json").bufferedReader()
                        .use { it.readText() }
                }

                tempGSON = Gson()
                val listLatestUpdate =
                    object : TypeToken<ArrayList<DashboardLatestUpdate>>() {}.type
                val latestUpdateList: ArrayList<DashboardLatestUpdate> =
                    tempGSON.fromJson(jsonParentUpdateString, listLatestUpdate)

                jsonString = application.assets.open("json/child.json").bufferedReader()
                    .use { it.readText() }
                tempGSON = Gson()
                val listPersonType = object : TypeToken<ArrayList<Student>>() {}.type
                val studentList: ArrayList<Student> = tempGSON.fromJson(jsonString, listPersonType)

                emit(
                    DataState.data(
                        data = DashboardViewState(
                            profile = userInfo,
                            eventData = eventData,
                            latestUpdateList = latestUpdateList,
                            childList = studentList
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                )
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        }
    }

    override fun getEventList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        withContext(IO) {
            var jsonString: String
            try {
                jsonString =
                    application.assets.open("json/event.json").bufferedReader()
                        .use { it.readText() }
                val gson = Gson()
                val listPersonType = object : TypeToken<ArrayList<Event>>() {}.type
                val eventList: ArrayList<Event> = gson.fromJson(jsonString, listPersonType)
                val selectedList: ArrayList<Event> = ArrayList();

                var isShowLess = false
                var nextEventCount = 4

                if (showOriginal) {
                    for (i in 0 until count) {
                        selectedList.add(eventList[i])
                    }
                    if (eventList.size < count + 4) {
                        nextEventCount = eventList.size - 3
                    }
                } else {
                    if (eventList.size > count) {
                        for (i in 0 until count) {
                            selectedList.add(eventList[i])
                        }
                        if (eventList.size <= count + nextEventCount) {
                            nextEventCount = eventList.size
                            isShowLess = true
                        }
                    } else {
                        for (i in 0 until eventList.size) {
                            selectedList.add(eventList[i])
                        }
                        nextEventCount = eventList.size - count
                        isShowLess = true
                    }
                }
                val eventData = EventData(isShowLess, nextEventCount, selectedList)
                emit(
                    DataState.data(
                        data = DashboardViewState(eventData = eventData),
                        stateEvent = stateEvent,
                        response = null
                    )
                )

            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }

        }
    }

    override fun getTodayResourceList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {

        withContext(IO) {
            val jsonString: String
            try {

                jsonString =
                    application.assets.open("json/todaysResource.json").bufferedReader()
                        .use { it.readText() }
                val gson = Gson()
                val listPersonType = object : TypeToken<ArrayList<DashboardResource>>() {}.type
                val eventList: ArrayList<DashboardResource> =
                    gson.fromJson(jsonString, listPersonType)

                val selectedList: ArrayList<DashboardResource> = ArrayList()


                var isShowLess = false
                var nextEventCount = 6

                if (showOriginal) {
                    for (i in 0 until count) {
                        selectedList.add(eventList[i])
                    }
                    if (eventList.size < count + 6) {
                        nextEventCount = eventList.size - 2
                    }
                } else {
                    if (eventList.size > count) {
                        for (i in 0 until count) {
                            selectedList.add(eventList[i])
                        }
                        if (eventList.size <= count + nextEventCount) {
                            nextEventCount = eventList.size
                            isShowLess = true
                        }
                    } else {
                        for (i in 0 until eventList.size) {
                            selectedList.add(eventList[i])
                        }
                        nextEventCount = eventList.size - count
                        isShowLess = true
                    }
                }
                val todayResourceData = TodaysResourceData(isShowLess, nextEventCount, selectedList)

                emit(
                    DataState.data(
                        data = DashboardViewState(todayResourceData = todayResourceData),
                        stateEvent = stateEvent,
                        response = null
                    )
                )

            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }

        }
    }

    override fun getLastViewedResourceList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        withContext(IO) {
            val jsonString: String
            try {
                jsonString =
                    application.assets.open("json/lastViewedResource.json").bufferedReader()
                        .use { it.readText() }
                val gson = Gson()
                val listPersonType = object : TypeToken<ArrayList<DashboardResourceType>>() {}.type
                val eventList: ArrayList<DashboardResourceType> =
                    gson.fromJson(jsonString, listPersonType)

                val selectedList: ArrayList<DashboardResourceType> = ArrayList();

                var isShowLess = false
                var nextEventCount = 10

                if (showOriginal) {
                    for (i in 0 until count) {
                        selectedList.add(eventList[i])
                    }
                    if (eventList.size < count + 10) {
                        nextEventCount = eventList.size - 2
                    }
                } else {
                    if (eventList.size > count) {
                        for (i in 0 until count) {
                            selectedList.add(eventList[i])
                        }
                        if (eventList.size <= count + nextEventCount) {
                            nextEventCount = eventList.size
                            isShowLess = true
                        }
                    } else {
                        for (i in 0 until eventList.size) {
                            selectedList.add(eventList[i])
                        }
                        nextEventCount = eventList.size - count
                        isShowLess = true
                    }
                }
                val lastViewResourceData =
                    LastViewResourceData(isShowLess, nextEventCount, selectedList)

                emit(
                    DataState.data(
                        data = DashboardViewState(lastViewedResourceData = lastViewResourceData),
                        stateEvent = stateEvent,
                        response = null
                    )
                )

            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        }
    }


    override fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>> = flow {
        withContext((IO)) {
            val userId = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            userDao.updateFaceIdLoginMode(checked, userId!!)
            emit(
                DataState.data(
                    data = DashboardViewState(isFaceLoginEnabled = checked),
                    stateEvent = stateEvent,
                    response = null
                )
            )
        }
    }

    override fun checkLoginMode(stateEvent: StateEvent): Flow<DataState<DashboardViewState>> =
        flow {
            withContext((IO)) {
                val userName =
                    sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_EMAIL, "")
                val userPassword =
                    sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD, "")

                Log.d("SAN", "userName-->$userName/userPassword-->$userPassword")
                if (userName != null && userPassword != null) {
                    val userInfo = userDao.getUserInfoData(userName, userPassword)
                    if (userInfo != null) {
                        Log.d("SAN", "fingerPrintMode-->${userInfo.fingerPrintMode}")
                        emit(
                            DataState.data(
                                data = DashboardViewState(
                                    isFingerPrintLoginEnabled = userInfo.fingerPrintMode,
                                    isFaceLoginEnabled = userInfo.faceIdMode
                                ),
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
    ): Flow<DataState<DashboardViewState>> = flow {
        withContext((IO)) {
            val userId = sharedPreferences.getString(PreferenceKeys.APP_PREFERENCES_KEY_USER_ID, "")
            val updatepasswordErrors = UpdatePasswordFields(
                old_password = oldPassword,
                new_password = newPassword,
                confirm_password = confirmPassword
            ).checkValidPassword()
            val dbOldPassword = userId?.let { userDao.getOldPassword(it) }
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
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
            } else if (!newPassword.equals(confirmPassword, false)) {
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
            } else if (oldPassword.isNotEmpty() && !dbOldPassword.equals(oldPassword, false)) {
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
            } else if (updatepasswordErrors != LoginFields.LoginError.none()) {
                Log.d("SAN", "emitting error: ${updatepasswordErrors}")
                emit(
                    buildError(
                        updatepasswordErrors,
                        UIComponentType.Dialog,
                        stateEvent
                    )
                )
            } else {
                userDao.updatePassword(newPassword, userId!!)
                sharedPrefsEditor.putString(
                    PreferenceKeys.APP_PREFERENCES_KEY_PASSWORD,
                    newPassword
                ).commit()
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
                userInfo.password = tempUserInfo.password
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

    override fun getUserClassLists(stateEvent: StateEvent): Flow<DataState<DashboardViewState>> =
        flow {
            var jsonString: String = ""
            try {

                jsonString = application.assets.open("json/class.json").bufferedReader()
                    .use { it.readText() }
                val gson = Gson()
                val listClass = object : TypeToken<List<ClassListsItem>>() {}.type
                var userClassList: List<ClassListsItem> = gson.fromJson(jsonString, listClass)

                emit(
                    DataState.data(
                        data = DashboardViewState(classList = userClassList),
                        stateEvent = stateEvent,
                        response = null
                    )
                )

            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }

        }

    override fun getChapterList(
        query: String,
        topicId: String,
        bookId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {
        val apiResult = safeCacheCall(IO) {
            if (query.isEmpty()) {
                subjectDao.getChapterListData(topicId, bookId)
            } else {
                subjectDao.getChapterListData(query, topicId, bookId)
            }
        }
        emit(
            object : CacheResponseHandler<SubjectViewState, List<Chapter>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<Chapter>): DataState<SubjectViewState> {
                    sharedPrefsEditor.putBoolean(
                        PreferenceKeys.APP_PREFERENCES_NEW_SESSION_BOOKS,
                        false
                    ).commit()

                    val chapterLearnData = ChapterLearnData(true,resultObj,null)
                    val viewState = SubjectViewState(chapterLearnData = chapterLearnData)
                    return DataState.data(
                        response = null,
                        data = viewState,
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )

    }

    override fun getTopicResourceList(
        query: String,
        topicId: String,
        chapterId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {

        val isNewSession = sharedPreferences.getBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_RESOURCES, true)
        if (isNewSession) {
            val apiResult = safeApiCall(IO) {
                tceService.getTopicResources()
            }
            emit(
                object : ApiResponseHandler<SubjectViewState, List<ResourceListResponse>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<ResourceListResponse>): DataState<SubjectViewState> {
                        val resourceList : ArrayList<Resource> = ArrayList()
                        withContext(IO) {
                            for (topicResourceItem in resultObj) {
                                try {
                                    // Launch each insert as a separate job to be executed in parallel
                                    launch {
                                        val resourceResponseList = topicResourceItem.resourceList

                                        for (resourceItem in resourceResponseList){
                                            val resource = resourceItem.toResource(topicResourceItem.id,topicResourceItem.id)
                                            resourceList.add(resource)
                                            try {
                                                subjectDao.insertResource(resource)
                                            } catch (e: Exception) {
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Could send an error report here or something but I don't think you should throw an error to the UI
                                    // Since there could be many blog posts being inserted/updated.
                                }
                            }
                        }
                        if (resultObj.isNotEmpty()) {
                            sharedPrefsEditor.putBoolean(PreferenceKeys.APP_PREFERENCES_NEW_SESSION_RESOURCES,false).commit()
                        }

                        val chapterLearnData = ChapterLearnData(false,null,subjectDao.getTopicResourceListData(topicId,chapterId))
                        val viewState = SubjectViewState(chapterLearnData = chapterLearnData)
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

                if(query.isEmpty()) {
                    subjectDao.getTopicResourceListData(topicId,chapterId)
                }else{
                    subjectDao.getTopicResourceListData(query,topicId,chapterId)
                }
            }
            emit(
                object : CacheResponseHandler<SubjectViewState, List<Resource>>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: List<Resource>): DataState<SubjectViewState> {
                        val chapterLearnData = ChapterLearnData(false,null,resultObj)
                        return DataState.data(
                            data = SubjectViewState(chapterLearnData = chapterLearnData),
                            response = null,
                            stateEvent = stateEvent
                        )
                    }
                }.getResult()
            )
        }
    }

    override fun getPlannerData(
        isShowLess: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>> = flow {
        var jsonString: String = ""
        try {

            val gson = Gson()
/*            jsonString = application.assets.open("json/lessonPlan.json").bufferedReader()
                .use { it.readText() }
            val listClass = object : TypeToken<List<LessonPlan>>() {}.type
            var list: List<LessonPlan> = gson.fromJson(jsonString, listClass)

            jsonString = application.assets.open("json/event.json").bufferedReader()
                .use { it.readText() }
            val listEvent = object : TypeToken<List<Event>>() {}.type
            var list1: List<Event> = gson.fromJson(jsonString, listEvent)


            jsonString = application.assets.open("json/birthday.json").bufferedReader()
                .use { it.readText() }
            val listBirthday = object : TypeToken<List<StudentBirthDay>>() {}.type
            var list2: List<StudentBirthDay> = gson.fromJson(jsonString, listBirthday)*/

            jsonString = application.assets.open("json/dailyPlanner.json").bufferedReader()
                .use { it.readText() }
            val listPlanner = object : TypeToken<List<DailyPlanner>>() {}.type
            var list3: List<DailyPlanner> = gson.fromJson(jsonString, listPlanner)

            for(j in list3.indices){
                val selectedEventList: ArrayList<Event> = ArrayList()
                val eventList = list3[j].eventList
                if (!isShowLess) {
                    if(eventList.size>2) {
                        for (i in 0 until 2) {
                            selectedEventList.add(eventList[i])
                        }
                    }else{
                        for (element in eventList) {
                            selectedEventList.add(element)
                        }
                    }

                } else {
                    for (element in eventList) {
                        selectedEventList.add(element)
                    }
                }
                val eventData = EventData(isShowLess, 0, selectedEventList)
                list3[j].eventData = eventData
            }

            jsonString = application.assets.open("json/child.json").bufferedReader()
                .use { it.readText() }
            val listPersonType = object : TypeToken<ArrayList<Student>>() {}.type
            val studentList: ArrayList<Student> = gson.fromJson(jsonString, listPersonType)

            emit(
                DataState.data(
                    data = PlannerViewState(dailyPlannerList = list3,
                                            childList = studentList
                    ),

                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    override fun getMonthlyPlannerData(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>> = flow {
        var jsonString: String = ""
        try {

            jsonString = application.assets.open("json/monthlyPlanner.json").bufferedReader()
                .use { it.readText() }
            val gson = Gson()
            val listClass = object : TypeToken<List<MonthlyPlanner>>() {}.type
            var list: List<MonthlyPlanner> = gson.fromJson(jsonString, listClass)

            val selectedList: List<MonthlyPlanner>;
            selectedList = if (TextUtils.isEmpty(query)) {
                list
            } else {
                list.filter { it.date.contains(query, ignoreCase = true) }
            }


            emit(
                DataState.data(
                    data = PlannerViewState(monthlyPlannerList = selectedList),
                    stateEvent = stateEvent,
                    response = null
                )
            )

        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    override fun getChapterResourceType(
        chapterId: String,
        topicId: String,
        bookId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>> = flow {
        val apiResult = safeCacheCall(IO) {
            subjectDao.getChapterResourceTypeData(chapterId,topicId,bookId)
        }
        emit(
            object : CacheResponseHandler<SubjectViewState, List<ChapterResourceType>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<ChapterResourceType>): DataState<SubjectViewState> {
                    val chapterResourceType = ChapterResourceType("ic_resources.svg","resource","Resource", emptyList(),"","",
                        chapterId,
                       topicId,
                        bookId)
                    val listOfResourceTpe: MutableList<ChapterResourceType> = mutableListOf()
                    listOfResourceTpe.addAll(resultObj)
                    listOfResourceTpe.add(chapterResourceType)
                    Log.d("SAN","listOfResourceTpe-->"+listOfResourceTpe.size)
                    Log.d("SAN","resultObj-->"+resultObj.size)

                    for (i in listOfResourceTpe.indices) {
                        var resourceList = subjectDao.getTopicResourceListData(listOfResourceTpe[i].id,listOfResourceTpe[i].chapterId)
                        if(listOfResourceTpe[i].id.equals("resource",true)){
                            resourceList = subjectDao.getTopicResourceListData(listOfResourceTpe[i].topicId,listOfResourceTpe[i].topicId)
                        }

                        listOfResourceTpe[i].resourceList = resourceList
                    }
                    return DataState.data(
                        data = SubjectViewState(chapterResourceTyeList =listOfResourceTpe),
                        response = null,
                        stateEvent = stateEvent
                    )
                }
            }.getResult()
        )

    }

    fun toGradeList(grades: List<GradeResponse>): List<Grade> {
        val gradeList: ArrayList<Grade> = ArrayList()
        for (gradeResponse in grades) {
            gradeList.add(gradeResponse.toGrade()
            )
        }
        return gradeList
    }

    fun toBookList(books: List<BookResponse>): List<Book> {
        val bookList: ArrayList<Book> = ArrayList()
        for (bookResponse in books) {
            bookList.add(bookResponse.toBook()
            )
        }
        return bookList
    }

}





