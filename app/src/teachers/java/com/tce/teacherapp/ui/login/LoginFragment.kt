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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
@kotlinx.coroutines.FlowPreview
class LoginFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_login) {
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
        val spanSupportMail = SpannableString(resources.getString(R.string.having_any_issues_drop_an_email_to_n_support_schoolname_com))
        val text = resources.getString(R.string.having_any_issues_drop_an_email_to_n_support_schoolname_com)
        val textSpan = resources.getString(R.string.support_mail)
        spanSupportMail.setSpan(Span(), text.indexOf(textSpan), text.length, 0)
        binding.tvSupportMail.text = spanSupportMail
        binding.tvSupportMail.movementMethod = LinkMovementMethod.getInstance()

        binding.edtSchoolName.setText(MessageConstant.LOGIN_DEFAULT_SCHOOLNAME)
        binding.edtUserName.setText(MessageConstant.LOGIN_DEFAULT_USERNAME)
        binding.edtPassword.setText(MessageConstant.LOGIN_DEFAULT_PASSWORD)

        binding.tvLogin.setOnClickListener {
            viewModel.setStateEvent(LoginStateEvent.LoginAttemptEvent(binding.edtUserName.text.toString().trim(),
                binding.edtPassword.text.toString().trim()))
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
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.loginFields?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(!it.login_mode_fingePrint_Enabled!!){
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
            ds.color = resources.getColor(R.color.login_support_mail)
            ds.isUnderlineText = false
        }

    }

}
