package com.tce.teacherapp.ui.dashboard.subjects

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.UICommunicationListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.lang.Exception

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseSubjectFragment(
    @LayoutRes
    private val layoutRes: Int,
    private val viewModelFactory: ViewModelProvider.Factory
    ) : Fragment(layoutRes) {

    val TAG: String = "AppDebug"

    val viewModel: SubjectsViewModel by viewModels {
        viewModelFactory
    }
    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChannel()
    }

     private fun setupChannel()  = viewModel.setupChannel()

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
}














