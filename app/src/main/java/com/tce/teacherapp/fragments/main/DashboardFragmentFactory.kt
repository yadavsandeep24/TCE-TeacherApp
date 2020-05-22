package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.home.DashboardHomeFragment
import javax.inject.Inject

class DashboardFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            DashboardHomeFragment::class.java.name -> {
                DashboardHomeFragment()
            }

            else -> {
                DashboardHomeFragment()
            }
        }


}