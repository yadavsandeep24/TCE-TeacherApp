package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.students.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentsFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {


    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            FeedbackFragment::class.java.name -> {
                FeedbackFragment(viewModelFactory)
            }

            PortfolioFragment::class.java.name -> {
                PortfolioFragment(viewModelFactory)
            }

            ProgressCardFragment::class.java.name -> {
                ProgressCardFragment(viewModelFactory)
            }

            StudentGalleryFragment::class.java.name -> {
                StudentGalleryFragment(viewModelFactory)
            }

            StudentGalleryImagePostDetailFragment::class.java.name -> {
                StudentGalleryImagePostDetailFragment(viewModelFactory)
            }

            StudentGalleryImagePostFragment::class.java.name -> {
                StudentGalleryImagePostFragment(viewModelFactory)
            }

            StudentGalleryShareChatSentFragment::class.java.name -> {
                StudentGalleryShareChatSentFragment(viewModelFactory)
            }

            StudentGalleryShareMediaFragment::class.java.name -> {
                StudentGalleryShareMediaFragment(viewModelFactory)
            }

            StudentGallerySharingChatFragment::class.java.name -> {
                StudentGallerySharingChatFragment(viewModelFactory)
            }

            StudentGalleryTaggedStudentFragment::class.java.name -> {
                StudentGalleryTaggedStudentFragment(viewModelFactory)
            }
            StudentGalleryTagStudentFragment::class.java.name -> {
                StudentGalleryTagStudentFragment(viewModelFactory)
            }

            StudentGalleryVideoPostDetailFragment::class.java.name -> {
                StudentGalleryVideoPostDetailFragment(viewModelFactory)
            }

            StudentGalleryVideoPostFragment::class.java.name -> {
                StudentGalleryVideoPostFragment(viewModelFactory)
            }

            StudentListFragment::class.java.name -> {
                StudentListFragment(viewModelFactory)
            }
            StudentProfileFragment::class.java.name -> {
                StudentProfileFragment(viewModelFactory)
            }

            StudentProfileShareChatFragment::class.java.name -> {
                StudentProfileShareChatFragment(viewModelFactory)
            }

            StudentProfileShareMediaFragment::class.java.name -> {
                StudentProfileShareMediaFragment(viewModelFactory)
            }

            StudentProfileUploadResourceSelectionFragment::class.java.name -> {
                StudentProfileUploadResourceSelectionFragment(viewModelFactory)
            }

            else -> {
                StudentListFragment(viewModelFactory)
            }
        }


}