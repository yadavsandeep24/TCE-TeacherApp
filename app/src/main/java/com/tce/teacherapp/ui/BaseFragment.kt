package com.tce.teacherapp.ui

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseFragment(
    @LayoutRes
    private val layoutRes: Int
) : Fragment(layoutRes) {

    val TAG: String = "AppDebug"


    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChannel()
        uiCommunicationListener.isStoragePermissionGranted()
    }

    abstract fun setupChannel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUICommunicationListener(null)

    }

    fun setUICommunicationListener(mockUICommuncationListener: UICommunicationListener?){

        // TEST: Set interface from mock
        if(mockUICommuncationListener != null){
            this.uiCommunicationListener = mockUICommuncationListener
        }
        else{ // PRODUCTION: if no mock, get from context
            try {
                uiCommunicationListener = (context as UICommunicationListener)
            }catch (e: Exception){
                Log.e("SubjectListfragment", "$context must implement UICommunicationListener")
            }
        }
    }
    fun setKeyboardVisibilityListener(parentView:View,onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            private var alreadyOpen = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
            private val rect: Rect = Rect()
            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                ).toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff: Int =
                    parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight
                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...")
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onVisibilityChanged(isShown)
            }
        })
    }
    interface OnKeyboardVisibilityListener {
        fun onVisibilityChanged(visible: Boolean)
    }
}














