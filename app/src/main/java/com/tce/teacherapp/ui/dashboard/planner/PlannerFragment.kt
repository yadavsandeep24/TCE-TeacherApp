package com.tce.teacherapp.ui.dashboard.planner

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentMessageListBinding
import com.tce.teacherapp.databinding.FragmentPlannerBinding
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.listeners.EventClickListener
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.messages.BaseMessageFragment
import com.tce.teacherapp.ui.dashboard.messages.adapter.messageListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.planner.adapter.dailyPlannerEpoxyHolder
import com.tce.teacherapp.ui.dashboard.planner.state.PLANNER_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerStateEvent
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.loadImage
import com.tce.teacherapp.util.Utility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class PlannerFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BasePlannerFragment(R.layout.fragment_planner, viewModelFactory), EventClickListener {

    private lateinit var binding : FragmentPlannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "PlannerViewState: inState is NOT null")
            (inState[PLANNER_VIEW_STATE_BUNDLE_KEY] as PlannerViewState?)?.let { viewState ->
                Log.d(TAG, "PlannerViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.dailyPlannerList = ArrayList()


        outState.putParcelable(
            MESSAGE_VIEW_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        viewModel.setStateEvent(PlannerStateEvent.GetPlannerData(""))

        binding.rvMainList.layoutManager = GridLayoutManager(activity, 1)
        binding.rvMainList.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvMainList)
        binding.rvMainList.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, model: SubjectListEpoxyHolder, _ ->
                requestManager.loadImage(model.imageUrl)
            }
        )

        binding.dailyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background = null
            binding.dailyContainer.background =  resources.getDrawable(R.drawable.bg_lessaon_daily_left)
            binding.tvDaily.setTextColor(resources.getColor(R.color.white))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.blue))
            binding.imgDaily.background =  resources.getDrawable(R.drawable.ic_day)
            binding.imgMonthly.background =  resources.getDrawable(R.drawable.ic_month_blue)


        })

        binding.monthlyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background = resources.getDrawable(R.drawable.bg_lessaon_daily_right)
            binding.dailyContainer.background = null
            binding.tvDaily.setTextColor(resources.getColor(R.color.blue))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.white))
            binding.imgDaily.background =  resources.getDrawable(R.drawable.ic_day_blue)
            binding.imgMonthly.background =  resources.getDrawable(R.drawable.ic_month)
            findNavController().navigate(
                R.id.action_plannerFragment_to_monthlyPlannerFragment
            )
        })

        subscribeObservers()

    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.dailyPlannerList?.let {
                    Log.d("SAN", "plannerList-->" + it.size)
                    binding.rvMainList.withModels {
                        for (msg in it) {
                            dailyPlannerEpoxyHolder {
                                id(msg.id.toLong())
                                dailyPlanner(msg)
                                evenClickListener(this@PlannerFragment)
                            }
                        }

                    }
                }


            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            // uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
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

    override fun onEventShowMoreClick(isShowLess: Boolean) {
        if (isShowLess) {
            viewModel.setStateEvent(
                PlannerStateEvent.GetPlannerEvent(
                    2,
                    true
                )
            )
        } else {
            viewModel.setStateEvent(
                PlannerStateEvent.GetPlannerEvent(
                    3,
                    false
                )
            )
        }
    }

    override fun onEventItemClick(event: Event) {
        Toast.makeText(requireContext(), "Click on ${event.type}", Toast.LENGTH_LONG).show()
    }

}