package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentStudentGalleryShareChatSentBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryShareChatSentFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_share_chat_sent, viewModelFactory) {

    private lateinit var binding : FragmentStudentGalleryShareChatSentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentStudentGalleryShareChatSentBinding.inflate(inflater,container,false)
        return binding.root
    }

}