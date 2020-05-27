package com.tce.teacherapp.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.TestTCEApplication
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.di.TestAppComponent
import com.tce.teacherapp.fragments.main.FakeSubjectsFragmentFactory
import com.tce.teacherapp.ui.dashboard.subjects.SubjectListFragment
import com.tce.teacherapp.ui.dashboard.subjects.setGradeListData
import com.tce.teacherapp.util.Constants.GRADES_DATA_FILENAME
import com.tce.teacherapp.util.JsonUtil
import com.tce.teacherapp.viewmodels.FakeSubjectViewModelFactory
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class DetailFragmentTest: BaseMainActivityTests() {

    private val CLASS_NAME = "DetailFragmentTest"

    @Inject
    lateinit var viewModelFactory: FakeSubjectViewModelFactory



    @Inject
    lateinit var jsonUtil: JsonUtil

    @FlowPreview
    @Inject
    lateinit var fragmentFactory: FakeSubjectsFragmentFactory

    val uiCommunicationListener = mockk<UICommunicationListener>()

    @Before
    fun init(){
        every { uiCommunicationListener.expandAppBar(true) } just runs
    }


    @Test
    fun is_selectedBlogPostDetailsSet() {
/*
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext.applicationContext as TestBaseApplication*/

        val app =  ApplicationProvider.getApplicationContext() as TestTCEApplication

        val apiService = configureFakeApiService(
            gradesDataSource = GRADES_DATA_FILENAME,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        fragmentFactory.uiCommunicationListener = uiCommunicationListener

        val scenario = launchFragmentInContainer<SubjectListFragment>(
            factory = fragmentFactory
        )

        val rawJson = jsonUtil.readJSONFromAsset(GRADES_DATA_FILENAME)
        val grades = Gson().fromJson<List<Grade>>(
            rawJson,
            object : TypeToken<List<Grade>>() {}.type
        )

        scenario.onFragment { fragment ->
            fragment.viewModel.setGradeListData(grades)
        }

    }

    override fun injectTest(application: TestTCEApplication) {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

}











