package com.tce.teacherapp.ui

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.tce.teacherapp.R
import com.tce.teacherapp.TestBaseApplication
import com.tce.teacherapp.di.TestAppComponent
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.util.Constants
import com.tce.teacherapp.util.Constants.GRADES_DATA_FILENAME
import com.tce.teacherapp.util.EspressoIdlingResourceRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Separate class for the error testing because because the error dialogs
 * are shown in MainActivity.
 * (ActivityScenario, not FragmentScenario.)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class ListFragmentTests: BaseMainActivityTests() {

    private val CLASS_NAME = "ListFragmentErrorTests"

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    @Test
    fun isErrorDialogShown_UnknownError() {
        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            gradesDataSource = GRADES_DATA_FILENAME,
            networkDelay = 0L,
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        val scenario = launchActivity<DashboardActivity>()

        val recyclerView = onView(withId(R.id.rv_subjects))

        recyclerView.check(matches(isDisplayed()))

        onView(withText("English")).check(matches(isDisplayed()))
    }


    @Test
    fun doesNetworkTimeout_networkTimeoutError() {

        val app = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext as TestBaseApplication

        val apiService = configureFakeApiService(
            gradesDataSource = GRADES_DATA_FILENAME,
            networkDelay = 4000L, // force timeout (4000 > 3000)
            application = app
        )

        configureFakeRepository(apiService, app)

        injectTest(app)

        val scenario = launchActivity<DashboardActivity>()

        onView(withText(R.string.text_error))
            .check(matches(isDisplayed()))

        onView(withSubstring(Constants.NETWORK_ERROR_TIMEOUT))
            .check(matches(isDisplayed()))

    }


    override fun injectTest(application: TestBaseApplication){
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

}













