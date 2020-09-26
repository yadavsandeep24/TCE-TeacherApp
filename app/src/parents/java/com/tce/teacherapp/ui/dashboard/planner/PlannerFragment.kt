package com.tce.teacherapp.ui.dashboard.planner

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentPlannerBinding
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.db.entity.LessonPlan
import com.tce.teacherapp.db.entity.LessonPlanPeriod
import com.tce.teacherapp.db.entity.LessonPlanResource
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.adapter.childEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.listeners.ChildClickListener
import com.tce.teacherapp.ui.dashboard.planner.adapter.dailyPlannerEpoxyHolder
import com.tce.teacherapp.ui.dashboard.planner.listeners.EventClickListener
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.ui.dashboard.planner.state.PLANNER_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerStateEvent
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.parents.fragment_dashboard_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private var selectedDate: String? = null

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
        selectedDate = arguments?.getString("date")
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideBottomBar(true)
        viewModel.setStateEvent(PlannerStateEvent.GetPlannerData(false,selectedDate))

        binding.rvMainList.layoutManager = GridLayoutManager(activity, 1)
        binding.rvMainList.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvMainList)

        binding.dailyContainer.setOnClickListener {
            binding.monthlyContainer.background = null
            binding.dailyContainer.background =
                resources.getDrawable(R.drawable.bg_lessaon_daily_left)
            binding.tvDaily.setTextColor(resources.getColor(R.color.white))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.blue))
            binding.imgDaily.background = resources.getDrawable(R.drawable.ic_day)
            binding.imgMonthly.background = resources.getDrawable(R.drawable.ic_month_blue)


        }

        binding.monthlyContainer.setOnClickListener {
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
        }

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

        binding.imgStudent.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                viewModel.setStateEvent(PlannerStateEvent.GetChildSelectedPosition)
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }

        binding.todayContainer.setOnClickListener {
            selectedDate = null
            viewModel.setStateEvent(PlannerStateEvent.GetPlannerData(false,null))
        }

        subscribeObservers()

    }


    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.dailyPlannerList?.let {
                    Log.d("SAN", "plannerList-->" + it.size)
                    if (it.isNotEmpty()) {
                        binding.tvNoData.visibility = View.GONE
                        binding.rvMainList.visibility = View.VISIBLE
                        binding.todayDateContainer.visibility = View.VISIBLE
                        binding.tvDate.text =
                            SimpleDateFormat("dd MMMM yyyy EEEE").format(Date())
                        binding.rvMainList.withModels {
                            for (dailyPlanner in it) {
                                dailyPlannerEpoxyHolder {

                                    if (SimpleDateFormat("dd MMMM yyyy EEEE").format(Date())
                                            .equals(dailyPlanner.date, ignoreCase = true)
                                    ) {
                                        binding.tvDate.text =
                                            SimpleDateFormat("dd MMMM yyyy EEEE").format(Date())
                                    }
                                    if (selectedDate != null) {
                                        val selectedLocalDate = LocalDate.parse(selectedDate)
                                        val formatter =
                                            DateTimeFormatter.ofPattern("dd MMMM yyyy EEEE")
                                        val formattedString: String =
                                            selectedLocalDate.format(formatter)
                                        binding.tvDate.text = formattedString
                                    }
                                    id(dailyPlanner.id.toLong())
                                    selectedDate(selectedDate)
                                    dailyPlanner(dailyPlanner)
                                    evenClickListener(this@PlannerFragment)
                                    lessonPLanClickListener(this@PlannerFragment)
                                }
                            }

                        }
                        binding.rvMainList.scrollToPosition(0)
                    }else{
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.rvMainList.visibility = View.GONE
                        binding.todayDateContainer.visibility = View.GONE
                    }
                }

                binding.rvFilter.withModels {
                    viewState.childList?.let {
                        for (child in it) {
                            childEpoxyHolder {
                                id(child.id)
                                strStudentName(child.Name)
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
        viewModel.setStateEvent(PlannerStateEvent.GetPlannerData(!isShowLess,selectedDate))
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

    override fun onMarkCompletedClick(lessonPlan: LessonPlan) {
        val bundle = Bundle()
        bundle.putParcelable("lessonPlanData", lessonPlan)
        findNavController().navigate(
            R.id.action_plannerFragment_to_markCompletedFragment,
            bundle
        )
    }

    override fun onResourceMarkCompletedChecked(resource: LessonPlanResource, isChecked : Boolean) {
        Toast.makeText(requireContext(), "Click on " , Toast.LENGTH_LONG).show()
    }

    override fun onLessonPlanResourceItemClick(resource: LessonPlanResource) {
        val bundle = Bundle()
        bundle.putString("title",resource.title)
        bundle.putString("url",resource.src)
        if(resource.src != null && resource.src.isNotEmpty()) {
            when {
                resource.contenttype.equals("av", true) -> {
                    bundle.putBoolean("isModality", true)
                    findNavController().navigate(
                        R.id.action_plannerFragment_to_videoPlayerFragment2,
                        bundle
                    )
                }
                resource.contenttype.equals("Image", true) -> {
                }
                resource.contenttype.equals("activity", true) -> {
                    findNavController().navigate(
                        R.id.action_plannerFragment_to_HTMLContentFragment2,
                        bundle
                    )
                }
                resource.contenttype.equals("audio", true) -> {
                }
                resource.contenttype.equals("ebook", true) -> {
                    findNavController().navigate(
                        R.id.action_plannerFragment_to_pdfFragment2,
                        bundle
                    )
                }
            }
        }
    }

    override fun onChildListItemClick(student: StudentListResponseItem) {
        viewModel.setStateEvent(PlannerStateEvent.SetChildSelectedPosition(student.id.toInt()-1))
        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
    }

}