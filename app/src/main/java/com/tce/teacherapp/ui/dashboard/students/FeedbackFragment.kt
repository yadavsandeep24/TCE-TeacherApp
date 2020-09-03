package com.tce.teacherapp.ui.dashboard.students

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentFeedbackBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.FeedbackAdapter
import com.tce.teacherapp.ui.dashboard.students.interfaces.MainInterface
import com.tce.teacherapp.ui.dashboard.students.state.STUDENT_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
        binding.rvFeedback.layoutManager = GridLayoutManager(activity, 2)
        binding.rvFeedback.setHasFixedSize(true)
        myAdapter = FeedbackAdapter(requireContext(), this)
        binding.rvFeedback.adapter = myAdapter

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
        binding.tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }
          viewModel.setStateEvent(StudentStateEvent.GetFeedbackMaster)

        subscribeObservers()


    }
    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.feedbackMaster?.let {
                    Log.d("SAN","it.feedbackList.size-->"+it.size)
                    myAdapter?.modelList = it.toMutableList()
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

    override fun mainInterface(size: Int) {
        if (size > 0) {
            binding.tvSave.isEnabled = true
            binding.tvSave.alpha =1f
        }else{
            binding.tvSave.isEnabled = false
            binding.tvSave.alpha =0.4f
        }
    }


}