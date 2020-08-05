package com.tce.teacherapp.ui.dashboard.students

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.edgedevstudio.example.recyclerviewmultiselect.MainInterface
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentFeedbackBinding
import com.tce.teacherapp.databinding.FragmentPortfolioBinding
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.AttendanceAdapter
import com.tce.teacherapp.ui.dashboard.students.adapter.FeedbackAdapter
import com.tce.teacherapp.ui.dashboard.students.adapter.PortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.STUDENT_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.ArrayList
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class FeedbackFragment  @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_feedback, viewModelFactory) , MainInterface {

    private lateinit var binding: FragmentFeedbackBinding

    companion object {
        var isMultiSelectOn = false
        val TAG = "MainActivity"
    }

    var myAdapter: FeedbackAdapter? = null

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
        binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        binding.rvFeedback.layoutManager = GridLayoutManager(activity, 2)
        binding.rvFeedback.setHasFixedSize(true)
        myAdapter = FeedbackAdapter(requireContext(), this)
        binding.rvFeedback.adapter = myAdapter
        myAdapter?.modelList = getDummyData()
        myAdapter?.notifyDataSetChanged()

        binding.tvSave.setOnClickListener {
            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_success_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            val txtTitle = dialog.findViewById(R.id.tv_title) as TextView

            txtTitle.text = "Award Feedback Updated\n For 2 Student"

            Handler().postDelayed({
                dialog.dismiss()
                (activity as DashboardActivity).onBackPressed()
            }, 1000)

        }


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

    override fun mainInterface(size: Int) {
        if (size > 0) {
           binding.tvSave.visibility = View.VISIBLE
        }else{
            binding.tvSave.visibility = View.GONE
        }
    }


}