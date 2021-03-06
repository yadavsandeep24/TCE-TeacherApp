package com.tce.teacherapp.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentRegisterInfoBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class RegisterInfoFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_register_info),BaseFragment.OnKeyboardVisibilityListener  {

    private var isPassWordVisible : Boolean = true

    private lateinit var binding: FragmentRegisterInfoBinding

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
        binding = FragmentRegisterInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setKeyboardVisibilityListener(binding.root,this)
        binding.tvRegister.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            viewModel.setStateEvent(LoginStateEvent.RegisterUserEvent(binding.edtMobileNo.text.toString().trim(),binding.edtPassword.text.toString().trim()))
        }
        val mobileNo = arguments?.getString("mobileNo")
        binding.edtMobileNo.setText(mobileNo)
        val relationType = arguments?.getString("relationType")

        if(!relationType.isNullOrEmpty()){
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

        val spanTerms= SpannableString(resources.getString(R.string.i_accept_the_term_amp_conditions))
         text = resources.getString(R.string.i_accept_the_term_amp_conditions)
        val textTermsSpan = resources.getString(R.string.term_amp_conditions)
        spanTerms.setSpan(TermSpan(), text.indexOf(textTermsSpan), text.length, 0)
        binding.tvTerms.text = spanTerms
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()

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


        binding.tvRegister.isEnabled = false
        binding.tvRegister.alpha = 0.4f

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.length<8) {
                    binding.tvRegister.isEnabled = false
                    binding.tvRegister.alpha = 0.4f
                } else {
                    if(binding.cbTerms.isChecked) {
                        binding.tvRegister.isEnabled = true
                        binding.tvRegister.alpha = 1.0f
                    }else{
                        binding.tvRegister.isEnabled = false
                        binding.tvRegister.alpha = 0.4f
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if(binding.edtPassword.text.toString().trim().length<8) {
                    binding.tvRegister.isEnabled = false
                    binding.tvRegister.alpha = 0.4f
                }else{
                    binding.tvRegister.isEnabled = true
                    binding.tvRegister.alpha = 1.0f
                }
            } else {
                binding.tvRegister.isEnabled = false
                binding.tvRegister.alpha = 0.4f
            }
        }

        subscribeObservers()
    }
    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState->

            if(viewState != null) {
                viewState.registerFields?.let {
                    viewState.registerFields = null
                    findNavController().navigate(R.id.action_registerInfoFragment_to_quickAccessSettingFragment)
                }
            }
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

    internal inner class TermSpan : ClickableSpan() {

        override fun onClick(tv: View) {
           // activity?.onBackPressed()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.terms_text)
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