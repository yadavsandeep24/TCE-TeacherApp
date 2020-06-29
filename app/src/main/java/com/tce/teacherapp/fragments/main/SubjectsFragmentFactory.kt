package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.subjects.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
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
            HTMLContentFragment::class.java.name -> {
                HTMLContentFragment(viewModelFactory)
            }
            PdfFragment::class.java.name -> {
                PdfFragment(viewModelFactory)
            }

            ResourceListFragment::class.java.name -> {
                ResourceListFragment(viewModelFactory)
            }

            SelectChapterBookFragment::class.java.name -> {
                SelectChapterBookFragment(viewModelFactory)
            }

            SelectChapterLearnFragment::class.java.name -> {
                SelectChapterLearnFragment(viewModelFactory)
            }

            SubjectPracticeFragment::class.java.name -> {
                SubjectPracticeFragment(viewModelFactory)
            }

            SubjectResourceDetailFragment::class.java.name -> {
                SubjectResourceDetailFragment(viewModelFactory)
            }

            SubjectResourceSelectionFragment::class.java.name -> {
                SubjectResourceSelectionFragment(viewModelFactory)
            }

            VideoPlayerFragment::class.java.name -> {
                VideoPlayerFragment(viewModelFactory)
            }

            else -> {
                SubjectListFragment(viewModelFactory)
            }
        }


}