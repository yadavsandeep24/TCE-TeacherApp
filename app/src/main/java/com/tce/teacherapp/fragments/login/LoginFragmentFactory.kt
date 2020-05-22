package com.tce.teacherapp.fragments.login

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.auth.LoginFragment
import javax.inject.Inject

class LoginFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            LoginFragment::class.java.name -> {
                LoginFragment(viewModelFactory)
            }

            else -> {
                LoginFragment(viewModelFactory)
            }
        }


}