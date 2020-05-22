package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.subjects.SubjectListFragment
import com.tce.teacherapp.ui.dashboard.subjects.TopicListFragment
import javax.inject.Inject

class SubjectsFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            SubjectListFragment::class.java.name -> {
                SubjectListFragment(viewModelFactory)
            }
            TopicListFragment::class.java.name -> {
                TopicListFragment(viewModelFactory)
            }

            else -> {
                SubjectListFragment(viewModelFactory)
            }
        }


}