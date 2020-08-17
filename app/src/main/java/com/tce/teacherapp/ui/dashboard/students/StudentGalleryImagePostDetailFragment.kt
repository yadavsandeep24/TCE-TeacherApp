package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentStudentGalleryImagePostDetailBinding
import javax.inject.Inject

class StudentGalleryImagePostDetailFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_image_post_detail, viewModelFactory) {

    private lateinit var binding : FragmentStudentGalleryImagePostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentStudentGalleryImagePostDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

}