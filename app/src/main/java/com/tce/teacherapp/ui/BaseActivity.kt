package com.tce.teacherapp.ui


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.tce.teacherapp.R
import com.tce.teacherapp.TCEApplication
import com.tce.teacherapp.ui.login.LauncherActivity
import com.tce.teacherapp.util.Constants.Companion.PERMISSIONS_REQUEST_READ_STORAGE
import com.tce.teacherapp.util.MessageType
import com.tce.teacherapp.util.Response
import com.tce.teacherapp.util.StateMessageCallback
import com.tce.teacherapp.util.UIComponentType

abstract class BaseActivity : AppCompatActivity(),
    UICommunicationListener {

    val TAG: String = "AppDebug"

    private var dialogInView: Dialog? = null

    abstract fun inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TCEApplication).appComponent
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    ) {

        when (response.uiComponentType) {

            is UIComponentType.AreYouSureDialog -> {

                response.message?.let {
                    areYouSureDialog(
                        message = it,
                        callback = response.uiComponentType.callback,
                        stateMessageCallback = stateMessageCallback
                    )
                }
            }

            is UIComponentType.Toast -> {
                response.message?.let {
                    displayToast(
                        message = it,
                        stateMessageCallback = stateMessageCallback
                    )
                }
            }

            is UIComponentType.Dialog -> {
                displayDialog(
                    response = response,
                    stateMessageCallback = stateMessageCallback
                )
            }

            is UIComponentType.None -> {
                // This would be a good place to send to your Error Reporting
                // software of choice (ex: Firebase crash reporting)
                Log.i(TAG, "onResponseReceived: ${response.message}")
                stateMessageCallback.removeMessageFromStack()
            }
        }
    }

    private fun displayDialog(
        response: Response,
        stateMessageCallback: StateMessageCallback
    ) {
        Log.d(TAG, "displayDialog: ")
        response.message?.let { message ->

            dialogInView = when (response.messageType) {

                is MessageType.Error -> {
                    displayCustomOkDialog(
                        message = message,
                        stateMessageCallback = stateMessageCallback
                    )
                }

                is MessageType.Success -> {
                    displayCustomOkDialog(
                        message = message,
                        stateMessageCallback = stateMessageCallback
                    )
                }

                is MessageType.Info -> {
                    displayCustomOkDialog(
                        message = message,
                        stateMessageCallback = stateMessageCallback
                    )
                }
                is MessageType.AccessDenied -> {
                    displayCustomAccessDeniedDialog(
                        message = message,
                        stateMessageCallback = stateMessageCallback
                    )
                }
                else -> {
                    // do nothing
                    stateMessageCallback.removeMessageFromStack()
                    null
                }
            }
        } ?: stateMessageCallback.removeMessageFromStack()
    }

    abstract override fun displayProgressBar(isLoading: Boolean)

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun isStoragePermissionGranted(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), PERMISSIONS_REQUEST_READ_STORAGE
            )
            false
        } else {
            // Permission has already been granted
            true
        }
    }

    override fun onPause() {
        super.onPause()
        if (dialogInView != null) {
            (dialogInView as MaterialDialog).dismiss()
            dialogInView = null
        }
    }


    private fun displayCustomOkDialog(
        message: String?,
        stateMessageCallback: StateMessageCallback
    ): Dialog {
        val dialog = Dialog(this, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_ok_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(resources.getColor(android.R.color.transparent))
        )
        dialog.show()

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        tvMessage.text = message
        val txtYes = dialog.findViewById(R.id.tv_ok) as TextView

        val ivDialogType = dialog.findViewById<ImageView>(R.id.iv_message_type)
        ivDialogType.background = resources.getDrawable(R.drawable.ic_baseline_error_outline_24)

        txtYes.setOnClickListener {
            dialog.dismiss()
            stateMessageCallback.removeMessageFromStack()
        }

        dialog.setOnDismissListener {
            dialogInView = null
        }

        return dialog
    }

    private fun displayCustomAccessDeniedDialog(
        message: String?,
        stateMessageCallback: StateMessageCallback
    ): Dialog {
        val dialog = Dialog(this, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_ok_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(resources.getColor(android.R.color.transparent))
        )
        dialog.show()

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
        tvMessage.text = message
        val txtYes = dialog.findViewById(R.id.tv_ok) as TextView

        val ivDialogType = dialog.findViewById<ImageView>(R.id.iv_message_type)
        ivDialogType.background = resources.getDrawable(R.drawable.ic_baseline_error_outline_24)

        txtYes.setOnClickListener {
            dialog.dismiss()
            stateMessageCallback.removeMessageFromStack()
            val i = Intent(this, LauncherActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        dialog.setOnDismissListener {
            dialogInView = null
        }

        return dialog
    }

    private fun areYouSureDialog(
        message: String,
        callback: AreYouSureCallback,
        stateMessageCallback: StateMessageCallback
    ): MaterialDialog {
        return MaterialDialog(this)
            .show {
                title(R.string.are_you_sure)
                message(text = message)
                negativeButton(R.string.text_cancel) {
                    callback.cancel()
                    stateMessageCallback.removeMessageFromStack()
                    dismiss()
                }
                positiveButton(R.string.text_yes) {
                    callback.proceed()
                    stateMessageCallback.removeMessageFromStack()
                    dismiss()
                }
                onDismiss {
                    dialogInView = null
                }
                cancelable(false)
            }
    }
}









