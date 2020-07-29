package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.home.AddChildFragment
import com.tce.teacherapp.ui.dashboard.home.DashboardHomeFragment
import com.tce.teacherapp.ui.dashboard.home.profile.ProfileFragment
import com.tce.teacherapp.ui.dashboard.home.profile.SettingsFragment
import com.tce.teacherapp.ui.dashboard.home.profile.TeacherProfileFragment
import com.tce.teacherapp.ui.dashboard.home.profile.UpdatePasswordFragment
import javax.inject.Inject

class DashboardFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            DashboardHomeFragment::class.java.name -> {
                DashboardHomeFragment(viewModelFactory)
            }

            SettingsFragment::class.java.name -> {
                SettingsFragment(viewModelFactory)
            }

            UpdatePasswordFragment::class.java.name -> {
                UpdatePasswordFragment(viewModelFactory)
            }

            TeacherProfileFragment::class.java.name -> {
                TeacherProfileFragment(viewModelFactory)
            }

            ProfileFragment::class.java.name -> {
                ProfileFragment(viewModelFactory)
            }
            AddChildFragment::class.java.name -> {
                AddChildFragment(viewModelFactory)
            }
            else -> {
                DashboardHomeFragment(viewModelFactory)
            }
        }


}