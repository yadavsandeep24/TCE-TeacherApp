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
import com.tce.teacherapp.ui.dashboard.subjects.SubjectListFragment
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

        var list = arrayOf("Junior KG A", "Junior KG B")
        var adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnClass.adapter = adapter


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
        }
    }

    override fun setupChannel() {
    }

}
