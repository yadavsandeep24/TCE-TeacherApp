package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.planner.EventDisplayFragment
import com.tce.teacherapp.ui.dashboard.planner.LessonPlanDisplayFragment
import com.tce.teacherapp.ui.dashboard.planner.MonthlyPlannerFragment
import com.tce.teacherapp.ui.dashboard.planner.PlannerFragment
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
                MonthlyPlannerFragment()
            }

            EventDisplayFragment::class.java.name -> {
                EventDisplayFragment()
            }

            LessonPlanDisplayFragment::class.java.name -> {
                LessonPlanDisplayFragment()
            }

            else -> {
                PlannerFragment(viewModelFactory)
            }
        }


}