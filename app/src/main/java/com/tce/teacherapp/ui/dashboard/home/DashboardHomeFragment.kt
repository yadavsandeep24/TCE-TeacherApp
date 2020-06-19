package com.tce.teacherapp.ui.dashboard.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentDashboardHomeBinding
import com.tce.teacherapp.db.entity.Division
import com.tce.teacherapp.fragments.main.CustomSpinnerAdapter
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.adapter.eventEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.adapter.headerEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.adapter.todayResourceEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.adapter.viewedResourceEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.SubjectListFragment
import kotlinx.android.synthetic.main.fragment_dashboard_home.*
import kotlinx.android.synthetic.main.message_bottom_filter.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class DashboardHomeFragment : BaseFragment(R.layout.fragment_dashboard_home) {

    private lateinit var binding : FragmentDashboardHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardHomeBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.imgSetting.setOnClickListener(View.OnClickListener {
            findNavController().navigate(
                R.id.action_dashboardHomeFragment_to_settingsFragment
            )
        })

        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.skipCollapsed = true

        bottomSheetBehavior.addBottomSheetCallback(object : com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val rotation = when (newState) {
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED -> 0f
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_COLLAPSED -> 180f
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN -> 180f
                    else -> return
                }

            }
        })
        binding.classContainer.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                binding.mainContainer.setBackgroundColor(resources.getColor(R.color.dim_color))
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
            } else {
                binding.mainContainer.setBackgroundColor(resources.getColor(R.color.dashboard_back))
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            }
        })



        binding.mainEpoxyRecycler.layoutManager = GridLayoutManager(activity, 1)
        binding.mainEpoxyRecycler.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.mainEpoxyRecycler)

        subscribeObservers()

    }

    private fun subscribeObservers() {
        binding.mainEpoxyRecycler.withModels {
            headerEpoxyHolder {
                id(1)
                strName("Hi Payal")
            }

            eventEpoxyHolder {
                id(1)
                strDate("15 Jan 2020")
            }

            todayResourceEpoxyHolder {
                id(1)
                strTitle("Today's Resources")

            }

            viewedResourceEpoxyHolder {
                id(1)
                strTitle("Last Viewed Resources")
            }


        }
    }

    override fun setupChannel() {
    }

}
