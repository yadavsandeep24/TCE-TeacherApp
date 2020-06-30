package com.tce.teacherapp.ui.home

import android.os.Build
import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentDashboardHomeBinding
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.dashboard.BaseDashboardFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.state.DASHBOARD_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.subjects.loadImage
import com.tce.teacherapp.ui.home.adapter.childEpoxyHolder
import com.tce.teacherapp.ui.home.adapter.eventEpoxyHolder
import com.tce.teacherapp.ui.home.adapter.latestUpdateEpoxyHolder
import com.tce.teacherapp.ui.home.adapter.parentHeaderEpoxyHolder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.parents.fragment_dashboard_home.*
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
        viewState?.eventData!!.eventList = ArrayList()
        viewState?.todayResourceData!!.todayResource = ArrayList()

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

        binding.mainEpoxyRecycler.layoutManager = GridLayoutManager(activity, 1)
        binding.mainEpoxyRecycler.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.mainEpoxyRecycler)

        binding.rvFilter.layoutManager = GridLayoutManager(activity, 1)
        binding.rvFilter.setHasFixedSize(true)
        val epoxyVisibilityTracker1 = EpoxyVisibilityTracker()
        epoxyVisibilityTracker1.attach(binding.rvFilter)

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
                        parentHeaderEpoxyHolder {
                            id(1)
                            strName("Hi ${it.name} !")
                            strLastSync("2 March 2020, 8 am")
                            val glide by lazy { Glide.with(binding.imgProfile.context) }
                            glide.loadImage(it.imageUrl).into(binding.imgProfile)
                            listener {
                                val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)
                                bottomSheetBehavior.addBottomSheetCallback(object :
                                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
                                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                                        if(newState == BottomSheetBehavior.STATE_DRAGGING){
                                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                        }

                                    }
                                })

                                if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                                    bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                                    binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                                    (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
                                } else {
                                    bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                                    binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                                    (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
                                }

                            }
                        }
                    }
                    viewState.eventData?.let {
                        eventEpoxyHolder {
                            id(2)
                            strDate("15 Jan 2020")
                            it.eventList?.let {
                                eventList(it)
                            }
                            listener {
                                //viewModel.setStateEvent(DashboardStateEvent.GetDashboardEvent(7, "event"))
                            }
                        }
                    }

                    viewState.latestUpdateList?.let {
                        latestUpdateEpoxyHolder {

                            id(3)
                            strTitle("Latest Update")
                            latestUpdateList(it)


                        }
                    }

                    binding.rvFilter.withModels {
                        viewState.childList?.let {
                            for (child in it) {
                                childEpoxyHolder {
                                    id(child.id)
                                    strStudentName(child.name)
                                }
                            }
                        }
                    }

                }
            }
        })
    }


}
