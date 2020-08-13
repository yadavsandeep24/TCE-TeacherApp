package com.tce.teacherapp.ui.dashboard.subjects

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentPdfBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class PdfFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_pdf,viewModelFactory) {

    lateinit var binding: FragmentPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        binding = FragmentPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideBottomBar(false)

        // Bind data.
        viewModel.pageInfo.observe(viewLifecycleOwner, Observer { (index, count) ->
            binding.tvTitle.text = "Title Name 3 - Modality"
            binding.exoPosition.text = (index + 1).toString()
            binding.exoDuration.text = count.toString()
           // activity?.title = getString(R.string.app_name_with_index, index + 1, count)
        })
        viewModel.pageBitmap.observe(viewLifecycleOwner, Observer { binding.image.setImageBitmap(it) })
        viewModel.previousEnabled.observe(viewLifecycleOwner, Observer {
            binding.exoPrev.isEnabled = it
        })
        viewModel.nextEnabled.observe(viewLifecycleOwner, Observer {
            binding.exoNext.isEnabled = it
        })

        // Bind events.
        binding.exoPrev.setOnClickListener { viewModel.showPrevious() }
        binding.exoNext.setOnClickListener { viewModel.showNext() }
        binding.ivClose.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.ibModality.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}