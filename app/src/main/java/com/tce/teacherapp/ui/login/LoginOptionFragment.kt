package com.tce.teacherapp.ui.login

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentLoginOptionBinding
import com.tce.teacherapp.databinding.FragmentSubjectListBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.showToast
import com.tce.teacherapp.util.Constants.Companion.DEFAULT_KEY_NAME
import com.tce.teacherapp.util.CustomAnimationDrawableNew
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.inject.Inject

class LoginOptionFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_login_option) {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvLoginManually = view.findViewById<TextView>(R.id.tv_login_manually)
        setupKeyStoreAndKeyGenerator()
        val defaultCipher: Cipher = setupCipher()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPrompt = createBiometricPrompt()
        }

        val tvLoginTouch = view.findViewById<TextView>(R.id.tv_login_touch_id)
        setUpTouchIDButton(tvLoginTouch,defaultCipher)

        tvLoginManually.setOnClickListener {
            findNavController().navigate(R.id.action_loginOptionFragment_to_loginFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(requireContext())

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "$errorCode :: $errString")

                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    findNavController().navigate(R.id.action_loginOptionFragment_to_loginFragment)
                }

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                val cad: CustomAnimationDrawableNew = object : CustomAnimationDrawableNew(
                    (resources.getDrawable(
                        R.drawable.thumb_animation_list
                    ) as AnimationDrawable)
                ) {
                    override fun onAnimationStart() {
                        // Animation has started...
                    }

                    override fun onAnimationFinish() {
                        val i = Intent(activity, DashboardActivity::class.java)
                        startActivity(i)
                        activity?.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                        activity?.finish()
                    }
                }
                binding.ivThumb.setBackgroundDrawable(cad)
                cad.start()


            }
        }

        val biometricPrompt = BiometricPrompt(this, executor, callback)
        return biometricPrompt
    }
    /**
     * Sets up default cipher and a non-invalidated cipher
     */
    private fun setupCipher(): Cipher{
        val defaultCipher: Cipher
        try {
            val cipherString = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            defaultCipher = Cipher.getInstance(cipherString)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException ->
                    throw RuntimeException("Failed to get an instance of Cipher", e)
                else -> throw e
            }
        }
        return defaultCipher
    }

    /**
     * Initialize the [Cipher] instance with the created key in the [createKey] method.
     *
     * @param keyName the key name to init the cipher
     * @return `true` if initialization succeeded, `false` if the lock screen has been disabled or
     * reset after key generation, or if a fingerprint was enrolled after key generation.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCipher(cipher: Cipher, keyName: String): Boolean {
        try {
            keyStore.load(null)
            cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(keyName, null) as SecretKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is KeyPermanentlyInvalidatedException -> return false
                is KeyStoreException,
                is CertificateException,
                is UnrecoverableKeyException,
                is IOException,
                is NoSuchAlgorithmException,
                is InvalidKeyException -> throw RuntimeException("Failed to init Cipher", e)
                else -> throw e
            }
        }
    }
    /**
     * Sets up KeyStore and KeyGenerator
     */
    private fun setupKeyStoreAndKeyGenerator() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchProviderException ->
                    throw RuntimeException("Failed to get an instance of KeyGenerator", e)
                else -> throw e
            }
        }
    }


    /**
     * Enables or disables purchase buttons and sets the appropriate click listeners.
     *
     * @param cipherNotInvalidated cipher for the not invalidated purchase button
     * @param defaultCipher the default cipher, used for the purchase button
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun setUpTouchIDButton(
        btnTouchID: TextView,
        defaultCipher: Cipher
    ) {

        if (BiometricManager.from(requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            createKey(DEFAULT_KEY_NAME)

            btnTouchID.run {
                isEnabled = true
                setOnClickListener(TouchIdClickListener(defaultCipher, DEFAULT_KEY_NAME))
            }
        } else {
            (activity as LauncherActivity).showToast(getString(R.string.setup_lock_screen))
            btnTouchID.isEnabled = false
        }
    }
    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with a fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if `false` is passed, the created key will not be
     * invalidated even if a new fingerprint is enrolled. The default value is `true` - the key will
     * be invalidated if a new fingerprint is enrolled.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun createKey(keyName: String) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of enrolled
        // fingerprints has changed.
        try {
            keyStore.load(null)

            val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val builder = KeyGenParameterSpec.Builder(keyName, keyProperties)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            keyGenerator.run {
                init(builder.build())
                generateKey()
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is CertificateException,
                is IOException -> throw RuntimeException(e)
                else -> throw e
            }
        }
    }

    private inner class TouchIdClickListener internal constructor(
        internal var cipher: Cipher,
        internal var keyName: String
    ) : View.OnClickListener {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onClick(view: View) {

            val promptInfo = createPromptInfo()

            if (initCipher(cipher, keyName)) {
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.prompt_info_title))
            .setSubtitle(getString(R.string.prompt_info_subtitle))
            .setDescription(getString(R.string.prompt_info_description))
            .setConfirmationRequired(false)
            .setNegativeButtonText("Login Manually")
            // .setDeviceCredentialAllowed(true) // Allow PIN/pattern/password authentication.
            // Also note that setDeviceCredentialAllowed and setNegativeButtonText are
            // incompatible so that if you uncomment one you must comment out the other
            .build()
        return promptInfo
    }


    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TAG = "LoginOptionFragment"
    }
}