package com.tce.teacherapp.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentVerifyOtpBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class VerifyOTPFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_verify_otp),BaseFragment.OnKeyboardVisibilityListener  {

    private lateinit var binding: FragmentVerifyOtpBinding

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
        binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setKeyboardVisibilityListener(binding.root,this)

        val relationType = arguments?.getString("relationType").toString()
        val isRegister = arguments?.getBoolean("isRegister")
        val mobileNo = arguments?.getString("mobileNo")

        binding.edtMobileNo.setText(mobileNo)

        if(relationType.isNotEmpty()) {
            when {
                relationType.equals("father",true) -> {
                    binding.user.background = resources.getDrawable(R.drawable.ic_father)

                }
                relationType.equals("mother",true) -> {
                    binding.user.background = resources.getDrawable(R.drawable.ic_mother)


                }
                relationType.equals("guardian",true) -> {
                    binding.user.background = resources.getDrawable(R.drawable.ic_guardian)

                }
            }
        }

        val spanBack= SpannableString("Back")
        var text = "Back"
        val textBackSpan = "Back"
        spanBack.setSpan(BackSpan(), text.indexOf(textBackSpan), text.length, 0)
        binding.tvBack.text = spanBack
        binding.tvBack.movementMethod = LinkMovementMethod.getInstance()

        val spanTerms= SpannableString(resources.getString(R.string.lbl_resend_otp))
        text = resources.getString(R.string.lbl_resend_otp)
        val textTermsSpan = resources.getString(R.string.lbl_resend_otp)
        spanTerms.setSpan(ResentOTPSpan(), text.indexOf(textTermsSpan), text.length, 0)
        binding.tvResendOtp.text = spanTerms
        binding.tvResendOtp.movementMethod = LinkMovementMethod.getInstance()

        binding.tvNext.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("relationType", relationType)
            bundle.putString("mobileNo",mobileNo)
            isRegister?.let { it1 -> bundle.putBoolean("isRegister", it1) }
            if(isRegister!!) {
                findNavController().navigate(
                    R.id.action_verifyOTPFragment_to_registerInfoFragment,
                    bundle
                )
            }else {
                findNavController().navigate(
                    R.id.action_verifyOTPFragment_to_newPasswordCreateFragment,
                    bundle
                )
            }
        }
        binding.vwLoop.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.tvNext.isEnabled = false
        binding.tvNext.alpha = 0.4f

        binding.edtOtp.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.length<4) {
                    binding.tvNext.isEnabled = false
                    binding.tvNext.alpha = 0.4f
                } else {
                    binding.tvNext.isEnabled = true
                    binding.tvNext.alpha = 1.0f
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
        })


        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->

            stateMessage?.let {
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
    internal inner class BackSpan : ClickableSpan() {

        override fun onClick(tv: View) {
            activity?.onBackPressed()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.orangey_red)
        }

    }

    internal inner class ResentOTPSpan : ClickableSpan() {

        override fun onClick(tv: View) {
            viewModel.setStateEvent(LoginStateEvent.ResentOTP(binding.edtMobileNo.text.toString()))
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.orangey_red)
        }

    }

    override fun onVisibilityChanged(visible: Boolean) {
        if (visible) {
            binding.flBottom.apply {
                alpha = 1f
                visibility = View.GONE
                animate()
                    .alpha(0f)
                    .setDuration(100)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.flBottom.visibility = View.GONE
                        }
                    })
            }
        }else{
            binding.flBottom.apply {
                alpha = 0f
                visibility = View.GONE
                animate()
                    .alpha(1f)
                    .setDuration(100)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.flBottom.visibility = View.VISIBLE
                        }
                    })
            }
        }
    }
}