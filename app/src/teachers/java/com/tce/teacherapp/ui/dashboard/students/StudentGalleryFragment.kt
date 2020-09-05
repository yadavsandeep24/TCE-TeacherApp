package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.api.response.StudentGalleryResponseItem
import com.tce.teacherapp.databinding.FragmentStudentGalleryBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.IStudentGalleryClickListener
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentGalleryAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery, viewModelFactory),
    IStudentGalleryClickListener {

    private lateinit var binding: FragmentStudentGalleryBinding
    private lateinit var classContainer: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(true)
        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object : com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        (activity as DashboardActivity).setCustomToolbar(R.layout.student_header_layout)

        classContainer =
            (activity as DashboardActivity).binding.toolBar.findViewById(R.id.class_container)
        val tvClassTitle =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_class_title) as TextView)

        tvClassTitle.text = "Gallery"

        classContainer.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.botomSheetSubFilterContainer.visibility = View.GONE
                binding.botomSheetMainFilterContainer.visibility = View.VISIBLE
                binding.maskLayout.visibility = View.VISIBLE
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }

        binding.tvPortfolio.setOnClickListener {
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_studentGalleryFragment_to_portfolioFragment)
        }

        binding.tvAttendance.setOnClickListener {
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_studentGalleryFragment_to_studentListFragment)
        }
        binding.tvGallary.setOnClickListener {
            bottomSheetBehavior.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
        }

        binding.sortContainer.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.botomSheetSubFilterContainer.visibility = View.VISIBLE
                binding.botomSheetMainFilterContainer.visibility = View.GONE
                binding.maskLayout.visibility = View.VISIBLE
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }
        binding.tvPhotos.setOnClickListener {
            bottomSheetBehavior.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetGalleryData(1))
        }

        binding.tvVideos.setOnClickListener {
            bottomSheetBehavior.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            viewModel.setStateEvent(StudentStateEvent.GetGalleryData(2))
        }

        binding.tvShowAll.setOnClickListener {
            bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            viewModel.setStateEvent(StudentStateEvent.GetGalleryData(0))
        }
        binding.btnUpload.setOnClickListener {
            findNavController().navigate(R.id.action_studentGalleryFragment_to_studentProfileUploadResouceSelectionFragment)
        }
        binding.maskLayout.setOnClickListener {
            bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
        }
        binding.btnShare.setOnClickListener {
            findNavController().navigate(R.id.action_studentGalleryFragment_to_studentGalleryShareMediaFragment)
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
                    val myAdapter = StudentGalleryAdapter(requireContext(),this,false)
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
        val bundle = Bundle()
        bundle.putParcelable("studentGalleryData",item)
        if(item.contenttype.equals("AV",true)) {
            findNavController().navigate(
                R.id.action_studentGalleryFragment_to_studentGalleryVideoPostFragment,
                bundle
            )
        }else if(item.contenttype.equals("image",true)){
            findNavController().navigate(
                R.id.action_studentGalleryFragment_to_studentGalleryImagePostFragment,
                bundle
            )
        }

    }

    override fun onCheckBoxClicked(date:String?,item: StudentGalleryData) {

    }

    override fun onDateCheckBoxClicked(item: StudentGalleryResponseItem) {
    }

}