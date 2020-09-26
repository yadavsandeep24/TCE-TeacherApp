package com.tce.teacherapp.ui.dashboard.messages

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.MessageListResponseItem
import com.tce.teacherapp.databinding.FragmentGroupInfoBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.groupInfoEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class GroupInfoFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseMessageFragment(R.layout.fragment_group_info, viewModelFactory) {

    private lateinit var binding : FragmentGroupInfoBinding

    var messageVo : MessageListResponseItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "MessageViewState: inState is NOT null")
            (inState[MESSAGE_VIEW_STATE_BUNDLE_KEY] as MessageViewState?)?.let { viewState ->
                Log.d(TAG, "MessageViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.

        viewState?.studentList = ArrayList()

        outState.putParcelable(
            MESSAGE_VIEW_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageVo = arguments?.getParcelable("messageData") as MessageListResponseItem?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)

        viewModel.setStateEvent(MessageStateEvent.GetStudentEvent(0,""))

        binding.imgClose.setOnClickListener {
            (activity as DashboardActivity).onBackPressed(false)
        }

        binding.tvTitle1.text = messageVo!!.Title

        if(messageVo!!.unReadCount == 0)
        {
            binding.tvSubTitle1.visibility= View.GONE
        }else{
            binding.tvSubTitle1.visibility= View.VISIBLE

        }
        binding.imgMessageDetail.background =
            requireContext().getDrawable(R.drawable.ic_dummy_class_apple)

/*        Utility.getDrawable(
            messageVo!!.icon.substring(
                0,
                messageVo!!.icon.lastIndexOf(".")
            ), requireContext()
        )?.let { it1 ->
            binding.imgMessageDetail.background = it1
        }*/

        binding.rvGroupInfo.layoutManager = GridLayoutManager(activity, 1)
        binding.rvGroupInfo.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvGroupInfo)
        subscribeObservers()

    }

    @SuppressLint("SetTextI18n")
    private fun subscribeObservers() {


        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentList?.let {
                    Log.d("SAN", "messageList-->" + it.size)
                    binding.tvSubTitle1.text = it.size.toString() + " Members"
                    binding.rvGroupInfo.withModels {
                        for (msg in it) {
                            groupInfoEpoxyHolder {
                                id(msg.id.toLong())
                                strStudentName(msg.Name)
                                listener {


                                }
                            }
                        }

                    }
                }


            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "SubjectListFragment-->viewModel.stateMessage")

            stateMessage?.let {

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })



    }




}
