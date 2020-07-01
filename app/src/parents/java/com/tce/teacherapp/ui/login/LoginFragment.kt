package com.tce.teacherapp.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentLoginBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.util.MessageConstant
import com.tce.teacherapp.util.StateMessageCallback
import com.tce.teacherapp.util.Utility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class LoginFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_login), BaseFragment.OnKeyboardVisibilityListener {
    var isPassWordVisible : Boolean = true

    private lateinit var binding: FragmentLoginBinding

    val viewModel: LoginViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SAN", "LoginFragment-->onViewCreated")
        val isForceFullLogin = arguments?.getBoolean("isForceFullLoginShow")
        Log.d("SAN", "isForceFullLogin-->$isForceFullLogin")
        setKeyboardVisibilityListener(binding.root,this)
        if (isForceFullLogin != null && isForceFullLogin) {
            binding.srContainer.visibility = View.VISIBLE
            binding.ivLogo.visibility = View.VISIBLE
            binding.flBottom.visibility = View.VISIBLE
            binding.dividerContainer.visibility = View.VISIBLE
            binding.tvRegister.visibility = View.VISIBLE
        } else {
            viewModel.setStateEvent(LoginStateEvent.PreUserInfoData)
        }

        val spanForgetPassword = SpannableString(resources.getString(R.string.lbl_forget_password))
        val text = resources.getString(R.string.lbl_forget_password)
        val textSpan = resources.getString(R.string.lbl_forget_password)
        spanForgetPassword.setSpan(Span(), text.indexOf(textSpan), text.length, 0)
        binding.tvForgotPassword.text = spanForgetPassword
        binding.tvForgotPassword.movementMethod = LinkMovementMethod.getInstance()

        binding.edtUserName.setText(MessageConstant.LOGIN_DEFAULT_USERNAME)
        binding.edtPassword.setText(MessageConstant.LOGIN_DEFAULT_PASSWORD)
        uiCommunicationListener.hideSoftKeyboard()

        binding.tvLogin.setOnClickListener {
            viewModel.setStateEvent(LoginStateEvent.LoginAttemptEvent(binding.edtUserName.text.toString().trim(),
                binding.edtPassword.text.toString().trim()))
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.vwPasswordVisibility.setOnClickListener {
            if(isPassWordVisible){
                isPassWordVisible = false
                binding.vwPasswordVisibility.background = resources.getDrawable(R.drawable.ic_baseline_visibility_off_24)
                binding.edtPassword.transformationMethod = SingleLineTransformationMethod()

            }else{
                isPassWordVisible = true
                binding.vwPasswordVisibility.background = resources.getDrawable(R.drawable.ic_baseline_visibility_24)
                binding.edtPassword.transformationMethod = PasswordTransformationMethod()
            }

        }
        binding.tvWelcome.text = Utility.getBannerDayMessage(requireActivity())
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.loginFields?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(it.isQuickAccessScreenShow!!){
                            viewState.loginFields = null
                            findNavController().navigate(R.id.action_loginFragment_to_quickAccessSettingFragment)
                        }else{
                            viewState.loginFields = null
                            val i = Intent(activity, DashboardActivity::class.java)
                            startActivity(i)
                            activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                            activity?.finish()
                        }

                    }else{
                      val i = Intent(activity, DashboardActivity::class.java)
                        startActivity(i)
                        activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                        activity?.finish()
                    }
                }

                if(viewState.profile == null) {
                    binding.srContainer.visibility = View.VISIBLE
                    binding.ivLogo.visibility = View.VISIBLE
                    binding.flBottom.visibility = View.VISIBLE
                    binding.dividerContainer.visibility = View.VISIBLE
                    binding.tvRegister.visibility = View.VISIBLE
                }else {
                    viewState.profile?.let {
                        if (it.faceIdMode || it.fingerPrintMode) {
                            viewState.profile = null
                            findNavController().navigate(R.id.action_loginFragment_to_loginOptionFragment)
                        } else {
                            binding.srContainer.visibility = View.VISIBLE
                            binding.ivLogo.visibility = View.VISIBLE
                            binding.flBottom.visibility = View.VISIBLE
                            binding.dividerContainer.visibility = View.VISIBLE
                            binding.tvRegister.visibility = View.VISIBLE
                            val userName = it.email
                            val password = it.password
                            binding.edtUserName.setText(userName)
                            binding.edtPassword.setText(password)
                            uiCommunicationListener.hideSoftKeyboard()
                        }
                    }
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "LoginFragment-->viewModel.stateMessage")

            stateMessage?.let {

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
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


    internal inner class Span : ClickableSpan() {

        override fun onClick(tv: View) {
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.forget_password_text)
        }

    }


    override fun onVisibilityChanged(visible: Boolean) {
        if (visible) {
            binding.flBottom.visibility = View.GONE
            binding.dividerContainer.visibility = View.GONE
            binding.tvRegister.visibility = View.GONE
        }else{
            binding.flBottom.visibility = View.VISIBLE
            binding.dividerContainer.visibility = View.VISIBLE
            binding.tvRegister.visibility = View.VISIBLE
        }
    }

}
