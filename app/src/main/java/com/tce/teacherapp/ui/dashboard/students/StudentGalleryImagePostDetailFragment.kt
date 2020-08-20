package com.tce.teacherapp.ui.dashboard.students

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.databinding.FragmentStudentGalleryImagePostDetailBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding =  FragmentStudentGalleryImagePostDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        val studentGallerydata = arguments?.getParcelable("studentGalleryData") as StudentGalleryData?
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE

        if (studentGallerydata != null) {
            Glide.with(requireContext())
                .load(studentGallerydata.image)
                .placeholder(R.drawable.dummy_video)
                .into(binding.ivContainer)
        }
        binding.imgCollapse.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("studentGalleryData",studentGallerydata)
           findNavController().navigate(R.id.action_studentGalleryImagePostDetailFragment_to_studentGalleryImagePostFragment,bundle)
        }
    }

}