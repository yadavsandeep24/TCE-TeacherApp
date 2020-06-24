package com.tce.teacherapp.ui.dashboard.home.profile

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentUpdatePasswordBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.DashboardViewModel
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.util.MessageConstant.Companion.RESPONSE_PASSWORD_UPDATE_SUCCESS
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class UpdatePasswordFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory
) : BaseFragment(R.layout.fragment_update_password) {

    private lateinit var binding: FragmentUpdatePasswordBinding

    val viewModel: DashboardViewModel by viewModels {
        viewModelFactory
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }

        (activity as DashboardActivity).setCustomToolbar(R.layout.parent_profile_header)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        val tvBack = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE

        val tvSave = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tvSave)
        tvSave.visibility = View.VISIBLE

        val tvTopicTitle = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)

        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }

        tvSave.setOnClickListener{
            viewModel.setStateEvent(DashboardStateEvent.UpdatePassword(binding.edtOldPassword.text.toString(),binding.edtNewPassword.text.toString(),binding.edtConfirmPassword.text.toString()))
        }

        tvTopicTitle.text = resources.getString(R.string.update_password)
        subscribeObservers()

    }
    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
            }
        })


        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->

            stateMessage?.let {
      /*          when(stateMessage.response.serviceTypes){

                    RequestTypes.GENERIC ->{
                        findNavController().navigate(R.id.action_updatePasswordFragment_to_settingsFragment)
                    }

                    else -> {

                    }
                }*/
                if(stateMessage.response.message.equals(RESPONSE_PASSWORD_UPDATE_SUCCESS)) {
                    uiCommunicationListener.hideSoftKeyboard()
                    findNavController().navigate(R.id.action_updatePasswordFragment_to_settingsFragment)
                }

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })
    }

    override fun setupChannel() {
        viewModel.setupChannel()
    }
}