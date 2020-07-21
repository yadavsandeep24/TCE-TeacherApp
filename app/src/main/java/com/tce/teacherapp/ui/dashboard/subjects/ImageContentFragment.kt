package com.tce.teacherapp.ui.dashboard.subjects

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentImageContentBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class ImageContentFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_image_content, viewModelFactory) {

    lateinit var binding: FragmentImageContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        binding = FragmentImageContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        val url = arguments?.getString("url")
        try {
            Glide.with(requireActivity())
                .load(url)
                .into(binding.ivContainer)
        }catch (e: Exception){
            e.printStackTrace()
        }


        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)
        (activity as DashboardActivity).showHideBottomBar(false)

        binding.ivCancel.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}