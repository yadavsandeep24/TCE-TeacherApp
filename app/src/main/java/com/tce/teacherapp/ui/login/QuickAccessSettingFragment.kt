package com.tce.teacherapp.ui.login

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentQuickAccessSettingBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import javax.inject.Inject

class QuickAccessSettingFragment
@Inject constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_quick_access_setting) {

    private lateinit var binding: FragmentQuickAccessSettingBinding

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
        binding = FragmentQuickAccessSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // binding.tvSkipForNow.paintFlags = binding.tvSkipForNow.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tvSkipForNow.setOnClickListener {
            val i = Intent(activity, DashboardActivity::class.java)
            startActivity(i)
            activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            activity?.finish()
        }
    }
}