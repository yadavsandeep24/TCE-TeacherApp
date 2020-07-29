package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.planner.*
import com.tce.teacherapp.ui.dashboard.subjects.HTMLContentFragment
import com.tce.teacherapp.ui.dashboard.subjects.ImageContentFragment
import com.tce.teacherapp.ui.dashboard.subjects.PdfFragment
import com.tce.teacherapp.ui.dashboard.subjects.VideoPlayerFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

class PlannerFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            PlannerFragment::class.java.name -> {
                PlannerFragment(viewModelFactory)
            }

            MonthlyPlannerFragment::class.java.name -> {
                MonthlyPlannerFragment(viewModelFactory)
            }

            EventDisplayFragment::class.java.name -> {
                EventDisplayFragment()
            }

            LessonPlanDisplayFragment::class.java.name -> {
                LessonPlanDisplayFragment()
            }
            MarkCompletedFragment::class.java.name -> {
                MarkCompletedFragment(viewModelFactory)
            }
            VideoPlayerFragment::class.java.name -> {
                VideoPlayerFragment(viewModelFactory)
            }
            HTMLContentFragment::class.java.name -> {
                HTMLContentFragment(viewModelFactory)
            }
            PdfFragment::class.java.name -> {
                PdfFragment(viewModelFactory)
            }
            ImageContentFragment::class.java.name ->{
                ImageContentFragment(viewModelFactory)
            }

            else -> {
                PlannerFragment(viewModelFactory)
            }
        }


}