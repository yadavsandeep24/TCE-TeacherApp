package com.tce.teacherapp.ui.login

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.TCEApplication
import com.tce.teacherapp.fragments.login.LoginNavHostFragment
import com.tce.teacherapp.ui.BaseActivity
import javax.inject.Inject
import javax.inject.Named

class LauncherActivity : BaseActivity() {

    @Inject
    @Named("LoginFragmentFactory")
    lateinit var loginFragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    override fun inject() {
        (application as TCEApplication).appComponent
            .injectLauncherActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        onRestoreInstanceState()
    }

    override fun displayProgressBar(isLoading: Boolean) {
    }

    override fun expandAppBar(value: Boolean) {
    }

    private fun onRestoreInstanceState() {
        val host = supportFragmentManager.findFragmentById(R.id.login_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost() {
        val navHost = LoginNavHostFragment.create(R.navigation.launcher_graph)

        supportFragmentManager.beginTransaction().replace(
                R.id.login_fragments_container,
                navHost,
                getString(R.string.LoginNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }
}
