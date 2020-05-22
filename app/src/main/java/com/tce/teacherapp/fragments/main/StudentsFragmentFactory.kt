package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.students.StudentListFragment
import javax.inject.Inject

class StudentsFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            StudentListFragment::class.java.name -> {
                StudentListFragment()
            }

            else -> {
                StudentListFragment()
            }
        }


}