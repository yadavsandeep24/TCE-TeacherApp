package com.tce.teacherapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import javax.inject.Inject

class QuickAccessSettingFragment
@Inject constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_quick_access_setting) {

    val viewModel: LoginViewModel by viewModels {
        viewModelFactory
    }
    override fun setupChannel() {
        viewModel.setupChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quick_access_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         val tvSkip = view.findViewById<TextView>(R.id.tv_skip_for_now)

        tvSkip.setOnClickListener {
            val i = Intent(activity, DashboardActivity::class.java)
            startActivity(i)
            activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            activity?.finish()
        }
    }
}