package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.edgedevstudio.example.recyclerviewmultiselect.MainInterface
import com.edgedevstudio.example.recyclerviewmultiselect.ViewHolderClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentPortfolioBinding
import com.tce.teacherapp.databinding.FragmentStudentListBinding
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.students.adapter.AttendanceAdapter
import com.tce.teacherapp.ui.dashboard.students.adapter.PortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.STUDENT_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.teachers.fragment_dashboard_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.ArrayList
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class PortfolioFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_portfolio, viewModelFactory), ViewHolderClickListener {

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var classContainer: LinearLayout
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
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent)

        val bottomSheetBehavior =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
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
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.class_container) as LinearLayout)
        val tvClassTitle =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_class_title) as TextView)
        tvClassTitle.setText("Portfolio")

        classContainer.setOnClickListener {
            viewModel.setStateEvent(DashboardStateEvent.GetUserClassList)
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

        binding.tvAttendance.setOnClickListener(View.OnClickListener {
            (activity as DashboardActivity).onBackPressed()
        })


        binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 3)
        binding.rvPortfolio.setHasFixedSize(true)
        myAdapter = PortfolioAdapter(requireContext(), this)
        binding.rvPortfolio.adapter = myAdapter
        myAdapter?.modelList = getDummyData()
        myAdapter?.notifyDataSetChanged()

        binding.tvFeedback.setOnClickListener(View.OnClickListener {
            myAdapter!!.setIsShowCheckBox(true)
            binding.nextContainer.visibility = View.VISIBLE
            binding.tvFeedback.visibility = View.GONE
        })
        binding.selectAllContainer.setOnClickListener(View.OnClickListener {
            if(binding.chkSelectall.isChecked == true){
                binding.chkSelectall.isChecked = false
                myAdapter!!.setIsSelectAll(false)
            }else{
                binding.chkSelectall.isChecked = true
                myAdapter!!.setIsSelectAll(true)
            }
        })

        binding.tvNext.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_portfolioFragment_to_feedbackFragment)
        })

        /* binding.maskLayout.setOnClickListener{
             if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED) {
                 bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                 binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                 (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
             }
         }*/


    }

    private fun getDummyData(): MutableList<Student> {
        Log.d(TAG, "inside getDummyData")
        val list = ArrayList<Student>()
        list.add(Student(1, "Student 1", "", false))
        list.add(Student(2, "Student 2", "", false))
        list.add(Student(3, "Student 3", "", false))
        list.add(Student(4, "Student 4", "", false))
        list.add(Student(5, "Student 5", "", false))
        list.add(Student(6, "Student 6", "", false))
        list.add(Student(7, "Student 7", "", false))
        list.add(Student(8, "Student 8", "", false))
        list.add(Student(9, "Student 9", "", false))
        list.add(Student(10, "Student 10", "", false))
        list.add(Student(11, "Student 11", "", false))
        list.add(Student(12, "Student 12", "", false))
        list.add(Student(13, "Student 13", "", false))


        Log.d(TAG, "The size is ${list.size}")
        return list
    }

    override fun onLongTap(index: Int) {

    }

    override fun onTap(index: Int) {
        findNavController().navigate(R.id.action_portfolioFragment_to_studentProfileFragment)
    }


}