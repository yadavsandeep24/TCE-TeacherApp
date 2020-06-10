package com.tce.teacherapp.ui.dashboard.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentDashboardHomeBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
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
    }

    override fun setupChannel() {
    }

}
