package com.tce.teacherapp.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentDashboardHomeBinding
import com.tce.teacherapp.db.entity.ClassListsItem
import com.tce.teacherapp.db.entity.DashboardResourceType
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.dashboard.BaseDashboardFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.adapter.*
import com.tce.teacherapp.ui.dashboard.home.listeners.ClassListClickListener
import com.tce.teacherapp.ui.dashboard.home.listeners.EventClickListener
import com.tce.teacherapp.ui.dashboard.home.listeners.LastViewedResourceClickListener
import com.tce.teacherapp.ui.dashboard.home.listeners.TodayResourceClickListener
import com.tce.teacherapp.ui.dashboard.home.state.DASHBOARD_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.teachers.fragment_dashboard_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class DashboardHomeFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseDashboardFragment(R.layout.fragment_dashboard_home, viewModelFactory),
    ClassListClickListener, EventClickListener , TodayResourceClickListener,
    LastViewedResourceClickListener {

    private lateinit var binding: FragmentDashboardHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "DashboardViewState: inState is NOT null")
            (inState[DASHBOARD_VIEW_STATE_BUNDLE_KEY] as DashboardViewState?)?.let { viewState ->
                Log.d(TAG, "DashboardViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.eventData!!.eventList = ArrayList()
        viewState.todayResourceData!!.todayResource = ArrayList()

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
        uiCommunicationListener.displayProgressBar(false)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        binding.imgSetting.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardHomeFragment_to_settingsFragment)
        }

        val bottomSheetBehavior =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_COLLAPSED
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

        binding.classContainer.setOnClickListener {
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

        /* binding.maskLayout.setOnClickListener{
             if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                 bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                 binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                 (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
             } else {
                 bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                 binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                 (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
             }
         }*/



        binding.mainEpoxyRecycler.layoutManager = GridLayoutManager(activity, 1)
        binding.mainEpoxyRecycler.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.mainEpoxyRecycler)

        binding.rvFilter.layoutManager = GridLayoutManager(activity, 1)
        binding.rvFilter.setHasFixedSize(true)
        val epoxyVisibilityTracker1 = EpoxyVisibilityTracker()
        epoxyVisibilityTracker1.attach(binding.rvFilter)

        viewModel.setStateEvent(DashboardStateEvent.GetTeacherDashboardData(1))

        subscribeObservers()

    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                binding.rvFilter.withModels {
                    viewState.classList?.let {
                        Log.d("SAN", "classList-->" + it.size)
                        for (item in it) {
                            classListEpoxyHolder {
                                id(item.id)
                                name(item.name)
                                imageUrl(item.imageUrl)
                                shortName(item.shortName)
                                Utility.getDrawable(
                                    item.imageUrl.substring(
                                        0,
                                        item.imageUrl.lastIndexOf(".")
                                    ), binding.rvFilter.context
                                )?.let { it1 ->
                                    imageDrawable(it1)
                                }
                                classListItem(item)
                                classListClickListener(this@DashboardHomeFragment)
                            }
                        }
                    }
                }


                binding.mainEpoxyRecycler.withModels {

                    viewState.profile?.let {
                        headerEpoxyHolder {
                            Log.d("SAN", "it.imageurl-->" + it.imageUrl)
                            id(1)
                            strName("Hi ${it.name} !")
                            strLastSync("2 March 2020, 8 am")
                            imageUrl(it.imageUrl)
                        }
                    }

                    viewState.eventData?.let {
                        eventEpoxyHolder {
                            id(2)
                            strDate("15 Jan 2020")
                            nextEventCount(it.nextEventCount)
                            showLessButton(it.isShowLess)
                            eventList(it.eventList)
                            evenClickListener(this@DashboardHomeFragment)
                        }
                    }

                    viewState.todayResourceData?.let {
                        todayResourceEpoxyHolder {
                            id(3)
                            strTitle("Today's Resources")
                            nextEventCount(it.nextEventCount)
                            showLessButton(it.isShowLess)
                            resourceList(it.todayResource)

                          todayResourceClickListener(this@DashboardHomeFragment)
                        }
                    }
                    viewState.lastViewedResourceData?.let {
                        viewedResourceEpoxyHolder {
                            id(4)
                            strTitle("Last Viewed Resources")
                            nextEventCount(it.nextEventCount)
                            showLessButton(it.isShowLess)
                            resourceList(it.lastViewResourceList)
                            lastViewedClickListener(this@DashboardHomeFragment)
                        }
                    }
                }
            }
        })
    }

    override fun onClassListItemClick(item: ClassListsItem) {
        binding.tvClassIcon.text = item.shortName
        Utility.getDrawable(
            item.imageUrl.substring(0, item.imageUrl.lastIndexOf(".")),
            binding.rvFilter.context
        )?.let { it1 ->
            binding.tvClassIcon.background = it1
        }
        binding.tvClassTitle.text = item.name
        val bottomSheetBehavior =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.state =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_COLLAPSED
        binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
        viewModel.setStateEvent(DashboardStateEvent.GetTeacherDashboardData(item.id.toInt()))
    }


    override fun onTodayResourceShowMoreClick(isShowLess: Boolean) {
        if (isShowLess) {
            viewModel.setStateEvent(
                DashboardStateEvent.GetTodayResource(
                    2,
                    true
                )
            )
        } else {
            viewModel.setStateEvent(
                DashboardStateEvent.GetTodayResource(
                    4,
                    false
                )
            )
        }
    }

    override fun onTodayResourceItemClick(dashboardResourceType: DashboardResourceType) {
        Toast.makeText(requireContext(), "Click on ${dashboardResourceType.title}", Toast.LENGTH_LONG).show()
    }

    override fun onEventShowMoreClick(isShowLess: Boolean) {
        if (isShowLess) {
            viewModel.setStateEvent(
                DashboardStateEvent.GetDashboardEvent(
                    3,
                    true
                )
            )
        } else {
            viewModel.setStateEvent(
                DashboardStateEvent.GetDashboardEvent(
                    7,
                    false
                )
            )
        }
    }

    override fun onEventItemClick(event: Event) {
        Toast.makeText(requireContext(), "Click on ${event.type}", Toast.LENGTH_LONG).show()
    }

    override fun onLastViewedResourceShowMoreClick(isShowLess: Boolean) {
        if (isShowLess) {
            viewModel.setStateEvent(
                DashboardStateEvent.GetLastViewedResource(
                    2, true
                )
            )
        } else {
            viewModel.setStateEvent(
                DashboardStateEvent.GetLastViewedResource(
                    10, false
                )
            )
        }
    }

    override fun onLastViewedItemClick(dashboardResourceTye : DashboardResourceType) {
        Toast.makeText(requireContext(), "Click on ${dashboardResourceTye.title}", Toast.LENGTH_LONG).show()
    }


}
