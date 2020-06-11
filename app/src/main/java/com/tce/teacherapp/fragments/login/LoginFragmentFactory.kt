package com.tce.teacherapp.fragments.login

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.login.LoginFragment
import com.tce.teacherapp.ui.login.LoginOptionFragment
import com.tce.teacherapp.ui.login.QuickAccessSettingFragment
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

            LoginOptionFragment::class.java.name ->{
                LoginOptionFragment(viewModelFactory)
            }

            QuickAccessSettingFragment::class.java.name ->{
                QuickAccessSettingFragment(viewModelFactory)
            }

            else -> {
                LoginFragment(viewModelFactory)
            }
        }


}