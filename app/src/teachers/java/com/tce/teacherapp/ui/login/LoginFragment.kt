package com.tce.teacherapp.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.tce.teacherapp.util.Utility.Companion.getBannerDayMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


/**
 * a simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
@kotlinx.coroutines.FlowPreview
class LoginFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory) : BaseFragment(R.layout.fragment_login),
    BaseFragment.OnKeyboardVisibilityListener {
    var isPassWordVisible: Boolean = true

    private lateinit var binding: FragmentLoginBinding

    val viewModel: LoginViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d("SAN", "LoginFragment-->onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SAN", "LoginFragment-->onViewCreated")
        setKeyboardVisibilityListener(binding.root, this)
        val isForceFullLogin = arguments?.getBoolean("isForceFullLoginShow")
        Log.d("SAN", "isForceFullLogin-->$isForceFullLogin")
        if (isForceFullLogin != null && isForceFullLogin) {
            binding.srContainer.visibility = View.VISIBLE
            binding.ivLogo.visibility = View.VISIBLE
        } else {
            viewModel.setStateEvent(LoginStateEvent.CheckLoginEnabledMode)
        }
        val spanSupportMail =
            SpannableString(resources.getString(R.string.having_any_issues_drop_an_email_to_n_support_schoolname_com))
        val text =
            resources.getString(R.string.having_any_issues_drop_an_email_to_n_support_schoolname_com)
        val textSpan = resources.getString(R.string.support_mail)
        spanSupportMail.setSpan(Span(), text.indexOf(textSpan), text.length, 0)
        binding.tvSupportMail.text = spanSupportMail
        binding.tvSupportMail.movementMethod = LinkMovementMethod.getInstance()

        binding.edtUserName.setText(MessageConstant.LOGIN_DEFAULT_USERNAME)
        binding.edtPassword.setText(MessageConstant.LOGIN_DEFAULT_PASSWORD)

        binding.tvLogin.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            viewModel.setStateEvent(
                LoginStateEvent.LoginAttemptEvent(
                    binding.edtSchoolName.text.toString().trim(),
                    binding.edtUserName.text.toString().trim(),
                    binding.edtPassword.text.toString().trim()
                )
            )
        }

        binding.vwPasswordVisibility.setOnClickListener {
            if (isPassWordVisible) {
                isPassWordVisible = false
                binding.vwPasswordVisibility.background =
                    resources.getDrawable(R.drawable.ic_baseline_visibility_off_24)
                binding.edtPassword.transformationMethod = SingleLineTransformationMethod()

            } else {
                isPassWordVisible = true
                binding.vwPasswordVisibility.background =
                    resources.getDrawable(R.drawable.ic_baseline_visibility_24)
                binding.edtPassword.transformationMethod = PasswordTransformationMethod()
            }

        }
        val arrayAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.school_name)
        )
        binding.edtSchoolName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length >2) {
                    viewModel.setStateEvent(
                        LoginStateEvent.SchoolNameEvent(
                            s.toString()
                        )
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        binding.edtSchoolName.setAdapter(arrayAdapter)
        binding.edtSchoolName.setOnClickListener { binding.edtSchoolName.showDropDown() }

        binding.tvWelcome.text = getBannerDayMessage(requireContext())
        viewModel.setStateEvent(LoginStateEvent.ClientIdEvent)
        subscribeObservers()

    }


    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.loginFields?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (it.isQuickAccessScreenShow!!) {
                            viewState.loginFields = null
                            findNavController().navigate(R.id.action_loginFragment_to_quickAccessSettingFragment)
                        } else {
                            viewModel.setAlarm(requireActivity())
                            viewState.loginFields = null
                            val i = Intent(activity, DashboardActivity::class.java)
                            startActivity(i)
                            activity?.overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            activity?.finish()
                        }

                    } else {
                        viewModel.setAlarm(requireActivity())
                        val i = Intent(activity, DashboardActivity::class.java)
                        startActivity(i)
                        activity?.overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        activity?.finish()
                    }
                }

                viewState.isFingerPrintLoginEnabled?.let {
                    if (it) {
                        viewState.isFingerPrintLoginEnabled = null
                        findNavController().navigate(R.id.action_loginFragment_to_loginOptionFragment)
                    } else {
                        binding.srContainer.visibility = View.VISIBLE
                        binding.ivLogo.visibility = View.VISIBLE
                    }
                }
                viewState.SchoolResponse?.let {
                    Log.d("SAN",it.size.toString())
                    val arrayAdapter = ArrayAdapter(
                        requireActivity(), android.R.layout.simple_dropdown_item_1line,
                        it
                    )
                    binding.edtSchoolName.setAdapter(arrayAdapter)
                    viewState.SchoolResponse = null
                }

                viewState.clientIdRes?.let {
                    Log.d("SAN","it.apiVersion-->"+it.apiVersion)
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
            ds.color = resources.getColor(R.color.orangey_red)
            ds.isUnderlineText = false
        }

    }

    override fun onVisibilityChanged(visible: Boolean) {

    }

}
