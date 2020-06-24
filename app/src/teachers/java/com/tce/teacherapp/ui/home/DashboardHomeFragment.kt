package com.tce.teacherapp.ui.home

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentDashboardHomeBinding
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.dashboard.BaseDashboardFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.adapter.eventEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.adapter.headerEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.adapter.todayResourceEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.adapter.viewedResourceEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.state.DASHBOARD_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
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
) : BaseDashboardFragment(R.layout.fragment_dashboard_home, viewModelFactory) {

    private lateinit var binding: FragmentDashboardHomeBinding

    private var eventList: ArrayList<Event> = ArrayList()

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
        viewState?.eventList = ArrayList()
        viewState?.todayResourceList = ArrayList()

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

        bottomSheetBehavior.addBottomSheetCallback(object :
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val rotation = when (newState) {
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED -> 0f
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_COLLAPSED -> 180f
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN -> 180f
                    else -> return
                }

            }
        })
        binding.classContainer.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                binding.mainContainer.setBackgroundColor(resources.getColor(R.color.dim_color))
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
            } else {
                binding.mainContainer.setBackgroundColor(resources.getColor(R.color.dashboard_back))
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            }
        }



        binding.mainEpoxyRecycler.layoutManager = GridLayoutManager(activity, 1)
        binding.mainEpoxyRecycler.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.mainEpoxyRecycler)

        //viewModel.setStateEvent(DashboardStateEvent.GetProfileEvent)

        viewModel.setStateEvent(DashboardStateEvent.GetDashboardData)
       // viewModel.setStateEvent(DashboardStateEvent.GetDashboardEvent(2, "today resource"))
       // viewModel.setStateEvent(DashboardStateEvent.GetDashboardEvent(2, "last viewed resource"))
        subscribeObservers()

    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                binding.mainEpoxyRecycler.withModels {

                    viewState.profile?.let {
                        headerEpoxyHolder {
                            Log.d("SAN","it.imageurl-->"+it.imageUrl)
                            id(1)
                            strName("Hi ${it.name} !")
                            strLastSync("2 March 2020, 8 am")
                            imageUrl(it.imageUrl)
                        }
                    }

                    viewState.eventList?.let {
                        eventEpoxyHolder {
                            id(2)
                            strDate("15 Jan 2020")
                            viewState.eventList?.let {
                                eventList(it)
                            }
                            listener {
                                viewModel.setStateEvent(DashboardStateEvent.GetDashboardEvent(7))
                            }
                        }
                    }

                    viewState.todayResourceList?.let {
                        todayResourceEpoxyHolder {
                            id(3)
                            strTitle("Today's Resources")
                            resourceList(it)
                            listener {
                                viewModel.setStateEvent(DashboardStateEvent.GetTodayResource(4))
                            }
                        }
                    }
                    viewState.lastViewedResourceList?.let {
                        viewedResourceEpoxyHolder {
                            id(4)
                            strTitle("Last Viewed Resources")
                            resourceList(it)
                            listener {
                                viewModel.setStateEvent(DashboardStateEvent.GetLastViewedResource(10))
                            }
                        }
                    }
                }
            }
        })
    }


}
