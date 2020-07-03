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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentMobileNumberBinding
import com.tce.teacherapp.ui.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class MobileNumberEnterFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_mobile_number),BaseFragment.OnKeyboardVisibilityListener  {

    private lateinit var binding: FragmentMobileNumberBinding

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
        binding = FragmentMobileNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setKeyboardVisibilityListener(binding.root,this)

        val relationType = arguments?.getString("relationType").toString()

        if(relationType.isNotEmpty()){
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
        binding.tvGetOtp.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            val bundle = Bundle()
            bundle.putString("relationType", relationType)
            arguments?.getBoolean("isRegister")?.let { it1 -> bundle.putBoolean("isRegister", it1) }
            bundle.putString("mobileNo",binding.edtMobileNo.text.toString().trim())
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_verifyOTPFragment,bundle)
        }
        val spanBack= SpannableString("Back")
        val text = "Back"
        val textBackSpan = "Back"
        spanBack.setSpan(BackSpan(), text.indexOf(textBackSpan), text.length, 0)
        binding.tvBack.text = spanBack
        binding.tvBack.movementMethod = LinkMovementMethod.getInstance()

        binding.tvGetOtp.isEnabled = false
        binding.tvGetOtp.alpha = 0.4f

        binding.edtMobileNo.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.length<10) {
                    binding.tvGetOtp.isEnabled = false
                    binding.tvGetOtp.alpha = 0.4f
                } else {
                    binding.tvGetOtp.isEnabled = true
                    binding.tvGetOtp.alpha = 1.0f
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    internal inner class BackSpan : ClickableSpan() {

        override fun onClick(tv: View) {
            activity?.onBackPressed()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.forget_password_text)
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