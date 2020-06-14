package com.tce.teacherapp.ui.login

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tce.teacherapp.R
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class BiometricHelper {

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private lateinit var keyStore: KeyStore
        private lateinit var keyGenerator: KeyGenerator

        /**
         * Sets up KeyStore and KeyGenerator
         */
        fun setupKeyStoreAndKeyGenerator() {
            try {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            } catch (e: KeyStoreException) {
                throw RuntimeException("Failed to get an instance of KeyStore", e)
            }

            try {
                keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEY_STORE
                )
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
         * Sets up default cipher and a non-invalidated cipher
         */
         fun setupCipher(): Cipher {
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

         fun createBiometricPrompt(context:Context,fragment: Fragment, listener:BiometricResponseListener): BiometricPrompt {
            val executor = ContextCompat.getMainExecutor(context)

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d("SAN", "$errorCode :: $errString")

                    listener.onBiometricAuthenticationError(errorCode,errString)

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    listener.onBiometricAuthenticationFailed()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("SAN", "Authentication was successful")
                    listener.onBiometricAuthenticationSucceeded(result)
                }
            }

            val biometricPrompt = BiometricPrompt(fragment, executor, callback)
            return biometricPrompt
        }

        /**
         * Initialize the [Cipher] instance with the created key in the [createKey] method.
         *
         * @param keyName the key name to init the cipher
         * @return `true` if initialization succeeded, `false` if the lock screen has been disabled or
         * reset after key generation, or if a fingerprint was enrolled after key generation.
         */
        fun initCipher(cipher: Cipher, keyName: String): Boolean {
            try {
                keyStore.load(null)
                cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(keyName, null) as SecretKey)
                return true
            } catch (e: Exception) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                return false
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
        fun createKey(keyName: String) {
            // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
            // for your flow. Use of keys is necessary if you need to know if the set of enrolled
            // fingerprints has changed.
            try {
                keyStore.load(null)

                val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val builder = KeyGenParameterSpec.Builder(keyName, keyProperties)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

                    keyGenerator.run {
                        init(builder.build())
                        generateKey()
                    }
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

        fun createPromptInfo(context: Context): BiometricPrompt.PromptInfo {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.prompt_info_title))
                .setSubtitle(context.getString(R.string.prompt_info_subtitle))
                .setDescription(context.getString(R.string.prompt_info_description))
                .setConfirmationRequired(false)
                .setNegativeButtonText("Login Manually")
                // .setDeviceCredentialAllowed(true) // Allow PIN/pattern/password authentication.
                // Also note that setDeviceCredentialAllowed and setNegativeButtonText are
                // incompatible so that if you uncomment one you must comment out the other
                .build()
            return promptInfo
        }
    }

    interface BiometricResponseListener{

        fun onBiometricAuthenticationError(errorCode: Int, errString: CharSequence)

        fun onBiometricAuthenticationFailed()

        fun onBiometricAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult)

    }
}