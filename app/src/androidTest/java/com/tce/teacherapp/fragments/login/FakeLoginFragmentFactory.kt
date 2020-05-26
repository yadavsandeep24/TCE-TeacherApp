package com.tce.teacherapp.fragments.login

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.UICommunicationListener
import com.tce.teacherapp.ui.login.LoginFragment
import javax.inject.Inject

class FakeLoginFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    // used for setting a mock<UICommunicationListener>
    lateinit var uiCommunicationListener: UICommunicationListener

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            LoginFragment::class.java.name -> {
                val fragment =  LoginFragment(viewModelFactory)
                if(::uiCommunicationListener.isInitialized){
                    fragment.setUICommunicationListener(uiCommunicationListener)
                }
                fragment
            }

            else -> {
                val fragment =  LoginFragment(viewModelFactory)
                if(::uiCommunicationListener.isInitialized){
                    fragment.setUICommunicationListener(uiCommunicationListener)
                }
                fragment
            }
        }


}