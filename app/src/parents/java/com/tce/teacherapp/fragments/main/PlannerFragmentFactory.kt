package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.planner.MonthlyPlannerFragment
import com.tce.teacherapp.ui.home.AddChildFragment
import com.tce.teacherapp.ui.planner.*
import javax.inject.Inject

class PlannerFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            PlannerFragment::class.java.name -> {
                PlannerFragment(viewModelFactory)
            }
            MonthlyPlannerFragment::class.java.name -> {
                MonthlyPlannerFragment(viewModelFactory)
            }

            AddChildFragment::class.java.name -> {
                AddChildFragment(viewModelFactory)
            }

            else -> {
                PlannerFragment(viewModelFactory)
            }
        }


}