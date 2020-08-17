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
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentPortfolioBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.PortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.STUDENT_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.teachers.fragment_dashboard_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class PortfolioFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_portfolio, viewModelFactory), ViewHolderClickListener {

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var classContainer: RelativeLayout
    var myAdapter: PortfolioAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "StudentViewState: inState is NOT null")
            (inState[STUDENT_VIEW_STATE_BUNDLE_KEY] as StudentViewState?)?.let { viewState ->
                Log.d(TAG, "StudentViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(true)
        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent)
        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
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
        tvClassTitle.text = "Portfolio"

        classContainer.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }

        binding.tvAttendance.setOnClickListener {
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
           findNavController().navigate(R.id.action_portfolioFragment_to_studentListFragment)
        }

        binding.tvPortfolio.setOnClickListener {
            bottomSheetBehavior.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
        }

        binding.tvGallary.setOnClickListener {
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            findNavController().navigate(R.id.action_portfolioFragment_to_studentGalleryFragment)
        }



        binding.tvFeedback.setOnClickListener {
            myAdapter!!.setIsShowCheckBox(true)
            binding.nextContainer.visibility = View.VISIBLE
            binding.tvFeedback.visibility = View.GONE
        }
        binding.selectAllContainer.setOnClickListener {
            if(binding.chkSelectall.isChecked){
                binding.chkSelectall.isChecked = false
                myAdapter!!.setIsSelectAll(false)
            }else{
                binding.chkSelectall.isChecked = true
                myAdapter!!.setIsSelectAll(true)
            }
        }

        binding.tvNext.setOnClickListener {
            findNavController().navigate(R.id.action_portfolioFragment_to_feedbackFragment)
        }

        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent)
        subscribeObservers()

    }


    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.studentListResponse?.let {
                    Log.d("SAN","it.studentList.size-->"+it.size)
                    StudentListFragment.isMultiSelectOn = false
                    binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 3)
                    binding.rvPortfolio.setHasFixedSize(true)
                    myAdapter = PortfolioAdapter(requireContext(), this)
                    binding.rvPortfolio.adapter = myAdapter
                    myAdapter?.modelList =it.toMutableList()
                    myAdapter?.notifyDataSetChanged()
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


    override fun onLongTap(index: Int) {

    }

    override fun onTap(index: Int,item : StudentListResponseItem?) {
        val bundle = Bundle()
        bundle.putParcelable("studentdata", item)
        findNavController().navigate(R.id.action_portfolioFragment_to_studentProfileFragment,bundle)
    }


}