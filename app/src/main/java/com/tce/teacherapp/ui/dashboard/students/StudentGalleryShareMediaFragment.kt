package com.tce.teacherapp.ui.dashboard.students

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.api.response.StudentGalleryResponseItem
import com.tce.teacherapp.databinding.FragmentStudentGalleryShareMediaBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.IStudentGalleryClickListener
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentGalleryAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_student_gallery.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryShareMediaFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_share_media, viewModelFactory),
    IStudentGalleryClickListener {

    private lateinit var binding : FragmentStudentGalleryShareMediaBinding
    private lateinit var  sharedPreference : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding  = FragmentStudentGalleryShareMediaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference =  requireActivity().getSharedPreferences("MARK_COUNT", Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
        editor.putInt("mark_count",0).apply()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)

        binding.toolbarTitle.text = "Share Media"

        binding.tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }
        binding.tvSend.setOnClickListener {
            findNavController().navigate(R.id.action_studentGalleryShareMediaFragment_to_studentGallerSharingChatFragment)
        }
        val bottomSheetBehaviorFilterContainer = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet_filter_by)

        bottomSheetBehaviorFilterContainer.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorFilterContainer.skipCollapsed = true
        bottomSheetBehaviorFilterContainer.isDraggable = false

        bottomSheetBehaviorFilterContainer.addBottomSheetCallback(object : com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehaviorFilterContainer.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.sortContainer.setOnClickListener {

            if (bottomSheetBehaviorFilterContainer.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }
        binding.tvPhotos.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            viewModel.setStateEvent(StudentStateEvent.GetGalleryData(1))
        }

        binding.tvVideos.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            viewModel.setStateEvent(StudentStateEvent.GetGalleryData(2))
        }

        binding.tvShowAll.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            viewModel.setStateEvent(StudentStateEvent.GetGalleryData(0))
        }
        viewModel.setStateEvent(StudentStateEvent.GetGalleryData(0))
        subscribeObservers()
    }
    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentgallerydata?.let {
                    binding.rvStudentGallery.layoutManager = GridLayoutManager(activity, 1)
                    binding.rvStudentGallery.setHasFixedSize(true)
                    val myAdapter = StudentGalleryAdapter(requireContext(),this,true)
                    binding.rvStudentGallery.adapter = myAdapter
                    myAdapter.modelList = it as MutableList<StudentGalleryResponseItem>
                    myAdapter.notifyDataSetChanged()
                }

            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "SubjectListFragment-->viewModel.stateMessage")

            stateMessage?.let {

                /*uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )*/
            }
        })

    }

    override fun onItemClick(item: StudentGalleryData) {
    }

    override fun onCheckBoxClicked(item: StudentGalleryData) {
        if(item.isSelected) {
            val count : Int  = sharedPreference.getInt("mark_count",0)
            editor.putInt("mark_count",count + 1)
            editor.commit()
        }else {
            val count : Int  = sharedPreference.getInt("mark_count",0)
            editor.putInt("mark_count",count - 1)
            editor.commit()
        }

        val count : Int  = sharedPreference.getInt("mark_count",0)
        if(count > 0){
            binding.tvSend.visibility = View.VISIBLE
            binding.tvCount.visibility = View.VISIBLE
            binding.tvCount.setText(count.toString())
        }  else{
            binding.tvSend.visibility = View.GONE
            binding.tvCount.visibility = View.GONE
        }
    }

    override fun onDateCheckBoxClicked(items: StudentGalleryResponseItem) {
        for (item in items.studentGalleryDataList){
            if(item.isSelected) {
                val count : Int  = sharedPreference.getInt("mark_count",0)
                editor.putInt("mark_count",count + 1)
                editor.commit()
            }else {
                val count : Int  = sharedPreference.getInt("mark_count",0)
                editor.putInt("mark_count",count - 1)
                editor.commit()
            }
        }
        val count : Int  = sharedPreference.getInt("mark_count",0)
        if(count > 0){
            binding.tvSend.visibility = View.VISIBLE
            binding.tvCount.visibility = View.VISIBLE
            binding.tvCount.setText(count.toString())
        }  else{
            binding.tvSend.visibility = View.GONE
            binding.tvCount.visibility = View.GONE
        }
    }

}