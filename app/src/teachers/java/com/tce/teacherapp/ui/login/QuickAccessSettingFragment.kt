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
import com.tce.teacherapp.databinding.FragmentQuickAccessSettingBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.login.state.LoginStateEvent
import com.tce.teacherapp.ui.showToast
import com.tce.teacherapp.util.Constants
import com.tce.teacherapp.util.CustomAnimationDrawableNew
import com.tce.teacherapp.util.MessageConstant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.crypto.Cipher
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class QuickAccessSettingFragment
@Inject constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_quick_access_setting),
    BiometricHelper.BiometricResponseListener {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var binding: FragmentQuickAccessSettingBinding

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
        // Inflate the layout for this fragment
        binding = FragmentQuickAccessSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var defaultCipher : Cipher? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BiometricHelper.setupKeyStoreAndKeyGenerator()
             defaultCipher = BiometricHelper.setupCipher()
            biometricPrompt = BiometricHelper.createBiometricPrompt(requireContext(), this, this)
        }
        binding.tvSkipForNow.setOnClickListener {
            val i = Intent(activity, DashboardActivity::class.java)
            startActivity(i)
            activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            activity?.finish()
        }

        binding.ivThumb.setOnClickListener {
            if (BiometricManager.from(requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                //viewModel.setFingerPrintEnableMode(true)
                BiometricHelper.createKey(Constants.DEFAULT_KEY_NAME)

                binding.ivThumb.run {
                    setOnClickListener(defaultCipher?.let { it1 ->
                        TouchIdClickListener(it1, Constants.DEFAULT_KEY_NAME)
                    })
                }
            } else {
                (activity as LauncherActivity).showToast(getString(R.string.setup_lock_screen))
            }
        }
        viewModel.setQuickAccessScreenShow(false)
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.loginFields?.let {
                    val i = Intent(activity, DashboardActivity::class.java)
                    startActivity(i)
                    activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    activity?.finish()
                }
            }
        })
    }
    private inner class TouchIdClickListener internal constructor(
        internal var cipher: Cipher,
        internal var keyName: String
    ) : View.OnClickListener {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onClick(view: View) {

            val promptInfo = BiometricHelper.createPromptInfo(requireContext())

            if (BiometricHelper.initCipher(cipher, keyName)) {
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errString: CharSequence) {
        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            findNavController().navigate(R.id.action_quickAccessSettingFragment_to_loginFragment)
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
                viewModel.setStateEvent(
                    LoginStateEvent.LoginAttemptEvent(
                        MessageConstant.LOGIN_DEFAULT_USERNAME,
                        MessageConstant.LOGIN_DEFAULT_USERNAME
                    )
                )
            }
        }
        binding.ivThumb.setBackgroundDrawable(cad)
        cad.start()
    }
}