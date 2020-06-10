package com.tce.teacherapp.ui.dashboard.home.profile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentSettingsBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity

class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)

        binding.editProfileContainer.setOnClickListener(View.OnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        })

        binding.updatePasswordContainer.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_updatePasswordFragment)
        })

    }

}