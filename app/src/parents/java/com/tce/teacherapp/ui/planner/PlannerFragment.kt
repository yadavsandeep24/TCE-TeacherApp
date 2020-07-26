package com.tce.teacherapp.ui.planner

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentPlannerBinding
import com.tce.teacherapp.db.entity.*
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.listeners.EventClickListener
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.planner.BasePlannerFragment
import com.tce.teacherapp.ui.planner.adapter.dailyPlannerEpoxyHolder
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.ui.dashboard.planner.state.PLANNER_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerStateEvent
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.ui.home.adapter.childEpoxyHolder
import com.tce.teacherapp.ui.home.listeners.ChildClickListener
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.parents.fragment_dashboard_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class PlannerFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BasePlannerFragment(R.layout.fragment_planner, viewModelFactory), EventClickListener,
    LessonPlanClickListener , ChildClickListener {

    private lateinit var binding: FragmentPlannerBinding

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

        viewModel.setStateEvent(PlannerStateEvent.GetPlannerData("2"))

        binding.rvMainList.layoutManager = GridLayoutManager(activity, 1)
        binding.rvMainList.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvMainList)

        binding.dailyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background = null
            binding.dailyContainer.background =
                resources.getDrawable(R.drawable.bg_lessaon_daily_left)
            binding.tvDaily.setTextColor(resources.getColor(R.color.white))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.blue))
            binding.imgDaily.background = resources.getDrawable(R.drawable.ic_day)
            binding.imgMonthly.background = resources.getDrawable(R.drawable.ic_month_blue)


        })

        binding.monthlyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background =
                resources.getDrawable(R.drawable.bg_lessaon_daily_right)
            binding.dailyContainer.background = null
            binding.tvDaily.setTextColor(resources.getColor(R.color.blue))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.white))
            binding.imgDaily.background = resources.getDrawable(R.drawable.ic_day_blue)
            binding.imgMonthly.background = resources.getDrawable(R.drawable.ic_month)
            findNavController().navigate(
                R.id.action_plannerFragment_to_monthlyPlannerFragment
            )
        })

        binding.rvFilter.layoutManager = GridLayoutManager(activity, 1)
        binding.rvFilter.setHasFixedSize(true)
        val epoxyVisibilityTracker1 = EpoxyVisibilityTracker()
        epoxyVisibilityTracker1.attach(binding.rvFilter)

        Utility.setSelectorRoundedCorner(
            requireContext(),  binding.addChild, 0,
            R.color.transparent, R.color.dim_color,
            R.color.transparent, R.color.transparent, 0
        )

        binding.addChild.setOnClickListener {
            findNavController().navigate(R.id.action_plannerFragment_to_addChildFragment2)
        }

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

        binding.imgStudent.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        })

        binding.todayContainer.setOnClickListener(View.OnClickListener {
            binding.rvMainList.scrollToPosition(0)
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
                                if(SimpleDateFormat("dd MMMM yyyy EEEE").format(Date()).equals(msg.date, ignoreCase = true)){
                                    binding.tvDate.setText(SimpleDateFormat("dd MMMM yyyy EEEE").format(Date()))
                                }
                                id(msg.id.toLong())
                                dailyPlanner(msg)
                                evenClickListener(this@PlannerFragment)
                                lessonPLanClickListener(this@PlannerFragment)
                            }
                        }

                    }
                }

                binding.rvFilter.withModels {
                    viewState.childList?.let {
                        for (child in it) {
                            childEpoxyHolder {
                                id(child.id)
                                strStudentName(child.name)
                                student(child)
                                childClickListener(this@PlannerFragment)
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
       /* if (isShowLess) {
            viewModel.setStateEvent(PlannerStateEvent.GetPlannerData("2"))
        } else {
            viewModel.setStateEvent(PlannerStateEvent.GetPlannerData("4"))
        }*/
    }

    override fun onEventItemClick(event: Event) {
       /* val bundle = Bundle()
        bundle.putParcelable("eventData", event)
        findNavController().navigate(
            R.id.action_plannerFragment_to_eventDisplayFragment,
            bundle
        )*/
    }

    override fun onLessonPlanClick(lessonPlanPeriod: LessonPlanPeriod) {
        /*val bundle = Bundle()
        bundle.putParcelable("lessonPlanData", lessonPlanPeriod)
        findNavController().navigate(
            R.id.action_plannerFragment_to_lessonPlanDisplayFragment,
            bundle
        )*/
    }

    override fun onMarkCompletedClick(lessonPlanPeriod: LessonPlanPeriod) {
        val bundle = Bundle()
        bundle.putParcelable("lessonPlanData", lessonPlanPeriod)
        findNavController().navigate(
            R.id.action_plannerFragment_to_markCompletedFragment,
            bundle
        )
    }

    override fun onResourceMarkCompletedChecked(resource: LessonPlanResource, isChecked : Boolean) {
        Toast.makeText(requireContext(), "Click on " , Toast.LENGTH_LONG).show()
    }

    override fun onChildListItemClick(student: Student) {
        //viewModel.setStateEvent(PlannerStateEvent.GetPlannerData(student.id.toString()))
        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
    }

}