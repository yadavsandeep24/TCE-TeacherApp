package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.home.DashboardHomeFragment
import com.tce.teacherapp.ui.dashboard.home.profile.SettingsFragment
import com.tce.teacherapp.ui.dashboard.home.profile.TeacherProfileFragment
import com.tce.teacherapp.ui.dashboard.home.profile.UpdatePasswordFragment
import com.tce.teacherapp.ui.dashboard.messages.GroupChatFragment
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

            SettingsFragment::class.java.name -> {
                SettingsFragment()
            }

            UpdatePasswordFragment::class.java.name -> {
                UpdatePasswordFragment()
            }

            TeacherProfileFragment::class.java.name -> {
                TeacherProfileFragment(viewModelFactory)
            }

            else -> {
                DashboardHomeFragment()
            }
        }


}