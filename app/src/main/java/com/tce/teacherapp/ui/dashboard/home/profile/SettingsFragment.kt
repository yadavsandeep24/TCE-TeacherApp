package com.tce.teacherapp.ui.dashboard.home.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentSettingsBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.login.LauncherActivity

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
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

        binding.imgBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.editProfileContainer.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_teacherProfileFragment)
        }

        binding.updatePasswordContainer.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_updatePasswordFragment)
        }

        binding.tvLogout.setOnClickListener {

            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_log_out)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                    resources
                        .getColor(android.R.color.transparent)
                )
            )
            dialog.show()

            val txtYes =
                dialog.findViewById(R.id.tvYes) as TextView
            val txtNo = dialog.findViewById(R.id.tvNo) as TextView

            txtYes.setOnClickListener {
                dialog.dismiss()
                val i = Intent(activity, LauncherActivity::class.java)
                startActivity(i)
                activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                activity?.finish()
            }

            txtNo.setOnClickListener {
                dialog.dismiss()
            }


        }

    }

}