package com.tce.teacherapp.ui.dashboard.home.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.biometric.BiometricManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentSettingsBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.DashboardViewModel
import com.tce.teacherapp.ui.dashboard.home.state.DASHBOARD_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.login.LauncherActivity
import com.tce.teacherapp.ui.showToast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class SettingsFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory
) : BaseFragment(R.layout.fragment_settings) {


    val viewModel: DashboardViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var binding: FragmentSettingsBinding

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
        (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        val topBar = (activity as DashboardActivity).binding.toolBar.findViewById<RelativeLayout>(R.id.top_container)
        topBar.setBackgroundColor(resources.getColor(R.color.setting_actionbar_color))

        val spnDivision =  (activity as DashboardActivity).binding.toolBar.findViewById<AppCompatSpinner>(R.id.spn_division)
        spnDivision.visibility = View.GONE

        val tvBack = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE

        val tvTopicTitle = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)

        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
        tvTopicTitle.text = resources.getString(R.string.title_settings)

        setFaceIdContainerVisibility()
        setUpdatePasswordVisibility()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && BiometricManager.from(requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)  {
            binding.tbFingerPrint.isEnabled = true
            binding.tbFingerPrint.alpha = 1.0f
        }else{
            binding.tbFingerPrint.isEnabled = false
            binding.tbFingerPrint.alpha = 0.4f
        }

        binding.tbFingerPrint.setOnCheckedChangeListener { _, isChecked ->
            if (BiometricManager.from(requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                viewModel.setStateEvent(DashboardStateEvent.FingerPrintEnableMode(isChecked))
            }else {
                (activity as DashboardActivity).showToast(getString(R.string.setup_lock_screen))
            }

        }

        binding.tbFaceid.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setStateEvent(DashboardStateEvent.FaceIdEnableMode(isChecked))
        }

        binding.editProfileContainer.setOnClickListener {
            if (resources.getString(R.string.app_type).equals(resources.getString(R.string.app_type_teacher),true)) {
                findNavController().navigate(R.id.action_settingsFragment_to_teacherProfileFragment)
            } else {
                findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
            }
        }

        binding.updatePasswordContainer.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_updatePasswordFragment)
        }

        binding.tvLogout.setOnClickListener {

            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_yesno_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            val txtYes = dialog.findViewById(R.id.tvYes) as TextView
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
        viewModel.setStateEvent(DashboardStateEvent.CheckLoginEnabledMode)
        subscribeObservers()
    }
    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.isFaceLoginEnabled?.let {
                    Log.d("SAN", "isFaceLoginEnabled-->$it")
                    binding.tbFaceid.isChecked = it
                }
                viewState.isFingerPrintLoginEnabled?.let {
                    Log.d("SAN", "isFingerPrintLoginEnabled-->$it")
                    binding.tbFingerPrint.isChecked = it
                }
            }
        })
    }
    override fun setupChannel() {
        viewModel.setupChannel()
    }

    private fun setUpdatePasswordVisibility() {
        if (resources.getBoolean(R.bool.is_show_face_id_login)) {
            binding.faceidContainer.visibility= View.VISIBLE
            binding.vwFaceidDivider.visibility= View.VISIBLE
        } else {
            binding.faceidContainer.visibility= View.GONE
            binding.vwFaceidDivider.visibility= View.GONE
        }
    }

    private fun setFaceIdContainerVisibility() {
        if (resources.getBoolean(R.bool.is_show_update_password)) {
            binding.updatePasswordContainer.visibility = View.VISIBLE
            binding.vwUpdatePasswordDivider.visibility = View.VISIBLE
        } else {
            binding.updatePasswordContainer.visibility = View.GONE
            binding.vwUpdatePasswordDivider.visibility = View.GONE
        }
    }

}