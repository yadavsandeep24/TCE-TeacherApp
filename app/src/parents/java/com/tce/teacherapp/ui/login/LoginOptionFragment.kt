package com.tce.teacherapp.ui.login

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentLoginOptionBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.login.BiometricHelper.Companion.createBiometricPrompt
import com.tce.teacherapp.ui.login.BiometricHelper.Companion.createPromptInfo
import com.tce.teacherapp.ui.login.BiometricHelper.Companion.setupCipher
import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.util.Constants.Companion.DEFAULT_KEY_NAME
import com.tce.teacherapp.util.CustomAnimationDrawableNew
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.crypto.Cipher
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class LoginOptionFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_login_option), BiometricHelper.BiometricResponseListener {
    private lateinit var biometricPrompt: BiometricPrompt

    private lateinit var binding: FragmentLoginOptionBinding

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
        binding = FragmentLoginOptionBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setStateEvent(LoginStateEvent.CheckLoginEnabledMode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BiometricHelper.setupKeyStoreAndKeyGenerator()
            val defaultCipher: Cipher = setupCipher()
            biometricPrompt = createBiometricPrompt(requireContext(),this,this)
            setUpTouchIDButton(defaultCipher)
        }else{
            binding.ivThumb.isEnabled = false
            binding.ivThumb.alpha =  0.4f
            binding.tvLoginTouchId.isEnabled = false
            binding.tvLoginTouchId.alpha = 0.4f
        }

        binding.tvLoginManually.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isForceFullLoginShow", true)
            findNavController().navigate(R.id.action_loginOptionFragment_to_loginFragment,bundle)
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.isFingerPrintLoginEnabled?.let {
                    if(it) {
                        binding.tvHint.text = resources.getString(R.string.lbl_you_can_use_touch_id_to_login)
                        binding.tvLoginTouchId.text = resources.getString(R.string.lbl_use_touch_id)
                        binding.ivThumb.background = resources.getDrawable(R.drawable.ic_thumbprint)
                    }else {
                        binding.tvHint.text = resources.getString(R.string.lbl_you_can_use_face_id_to_login)
                        binding.tvLoginTouchId.text = resources.getString(R.string.lbl_use_face_id)
                        binding.ivThumb.background = resources.getDrawable(R.drawable.ic_face_id)
                    }
                }
                viewState.isFaceLoginEnabled?.let {
                    if(!it) {
                        binding.tvHint.text = resources.getString(R.string.lbl_you_can_use_touch_id_to_login)
                        binding.tvLoginTouchId.text = resources.getString(R.string.lbl_use_touch_id)
                        binding.ivThumb.background = resources.getDrawable(R.drawable.ic_thumbprint)
                    }else {
                        binding.tvHint.text = resources.getString(R.string.lbl_you_can_use_face_id_to_login)
                        binding.tvLoginTouchId.text = resources.getString(R.string.lbl_use_face_id)
                        binding.ivThumb.background = resources.getDrawable(R.drawable.ic_face_id)
                    }
                }
            }
        })
    }
    private fun setUpTouchIDButton(defaultCipher: Cipher) {

        if (BiometricManager.from(requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            BiometricHelper.createKey(DEFAULT_KEY_NAME)

            binding.ivThumb.run {
                isEnabled = true
                binding.ivThumb.alpha = 1.0f
                setOnClickListener(TouchIdClickListener(defaultCipher))

                binding.tvLoginTouchId.setOnClickListener {
                    val promptInfo = createPromptInfo(requireContext())
                    if(binding.tvLoginTouchId.text.toString().equals(resources.getString(R.string.lbl_use_face_id),true)){
                        viewModel.setStateEvent(LoginStateEvent.FaceIdEnableMode(true))
                        biometricPrompt.authenticate(promptInfo)
                    }else{
                        viewModel.setStateEvent(LoginStateEvent.FingerPrintEnableMode(true))
                        if (BiometricHelper.initCipher(defaultCipher, DEFAULT_KEY_NAME)) {
                            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(defaultCipher))
                        }
                    }
                }
            }
        } else {
            binding.ivThumb.isEnabled = false
            binding.ivThumb.alpha = 0.4f
            binding.tvLoginTouchId.isEnabled = false
            binding.tvLoginTouchId.alpha = 0.4f

        }
    }

    private inner class TouchIdClickListener internal constructor(
        internal var cipher: Cipher
    ) : View.OnClickListener {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onClick(view: View) {
            val promptInfo = createPromptInfo(requireContext())
            if(binding.tvLoginTouchId.text.toString().equals(resources.getString(R.string.lbl_use_face_id),true)){
                viewModel.setStateEvent(LoginStateEvent.FaceIdEnableMode(true))
                biometricPrompt.authenticate(promptInfo)
            }else{
                viewModel.setStateEvent(LoginStateEvent.FingerPrintEnableMode(true))
                if (BiometricHelper.initCipher(cipher, DEFAULT_KEY_NAME)) {
                    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
                }
            }
        }
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errString: CharSequence) {
        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            findNavController().navigate(R.id.action_loginOptionFragment_to_loginFragment)
        }
    }

    override fun onBiometricAuthenticationFailed() {
    }

    override fun onBiometricAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        val cad: CustomAnimationDrawableNew = object : CustomAnimationDrawableNew(
            (context?.resources?.getDrawable(
                R.drawable.thumb_animation_list
            ) as AnimationDrawable)
        ) {
            override fun onAnimationStart() {
                // Animation has started...
            }

            override fun onAnimationFinish() {
                val i = Intent(activity, DashboardActivity::class.java)
                startActivity(i)
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                activity?.finish()
            }
        }
        binding.ivThumb.setBackgroundDrawable(cad)
        cad.start()
    }

}