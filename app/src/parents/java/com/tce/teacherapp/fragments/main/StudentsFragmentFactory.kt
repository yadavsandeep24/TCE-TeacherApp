package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.home.AddChildFragment
import com.tce.teacherapp.ui.dashboard.messages.GroupChatFragment
import com.tce.teacherapp.ui.dashboard.messages.ResourceFragment
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


            ProgressCardFragment::class.java.name -> {
                ProgressCardFragment(viewModelFactory)
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

            StudentProfileFragment::class.java.name -> {
                StudentProfileFragment(viewModelFactory)
            }


            StudentProfileUploadResourceSelectionFragment::class.java.name -> {
                StudentProfileUploadResourceSelectionFragment(viewModelFactory)
            }
            GroupChatFragment::class.java.name -> {
                GroupChatFragment(viewModelFactory)
            }
            ResourceFragment::class.java.name -> {
                ResourceFragment(viewModelFactory)
            }
            AddChildFragment::class.java.name -> {
                AddChildFragment(viewModelFactory)
            }

            AbsentDaysCalenderFragment::class.java.name -> {
                AbsentDaysCalenderFragment(viewModelFactory)
            }
            StudentProfileShareMediaFragment::class.java.name -> {
                StudentProfileShareMediaFragment(viewModelFactory)
            }
            else -> {
                StudentProfileFragment(viewModelFactory)
            }
        }


}