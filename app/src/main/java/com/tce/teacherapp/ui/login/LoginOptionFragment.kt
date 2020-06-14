package com.tce.teacherapp.ui.login

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        viewModel.setStateEvent(LoginStateEvent.CheckFingerPrintLoginEnabled)
        val tvLoginManually = view.findViewById<TextView>(R.id.tv_login_manually)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BiometricHelper.setupKeyStoreAndKeyGenerator()
            val defaultCipher: Cipher = setupCipher()
            biometricPrompt = createBiometricPrompt(requireContext(),this,this)
            setUpTouchIDButton(defaultCipher)
        }else{
            binding.ivThumb.isEnabled = false
            binding.ivThumb.alpha =  0.4f
        }

        tvLoginManually.setOnClickListener {
            findNavController().navigate(R.id.action_loginOptionFragment_to_loginFragment)
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.isFingerPrintLoginEnabled?.let {
                   if(!it) {
                       findNavController().navigate(R.id.action_loginOptionFragment_to_loginFragment)
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
                setOnClickListener(TouchIdClickListener(defaultCipher, DEFAULT_KEY_NAME))
            }
        } else {
            binding.ivThumb.isEnabled = false
            binding.ivThumb.alpha = 0.4f
        }
    }

    private inner class TouchIdClickListener internal constructor(
        internal var cipher: Cipher,
        internal var keyName: String
    ) : View.OnClickListener {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onClick(view: View) {

            val promptInfo = createPromptInfo(requireContext())

            if (BiometricHelper.initCipher(cipher, keyName)) {
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
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