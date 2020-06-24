package com.tce.teacherapp.ui.login

import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentRegisterInfoBinding
import com.tce.teacherapp.ui.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class RegisterInfoFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_register_info) {

    var isPassWordVisible : Boolean = true

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
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_registerInfoFragment_to_quickAccessSettingFragment)
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

    internal inner class TermSpan : ClickableSpan() {

        override fun onClick(tv: View) {
           // activity?.onBackPressed()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = resources.getColor(R.color.terms_text)
        }

    }

}