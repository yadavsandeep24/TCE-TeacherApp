package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.calender.CalenderHomeFragment
import javax.inject.Inject

class CalenderFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            CalenderHomeFragment::class.java.name -> {
                CalenderHomeFragment()
            }

            else -> {
                CalenderHomeFragment()
            }
        }


}