package com.tce.teacherapp.fragments.login

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.login.*
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

            RegisterFragment::class.java.name ->{
                RegisterFragment(viewModelFactory)
            }
            ChildRelationFragment::class.java.name ->{
                ChildRelationFragment(viewModelFactory)
            }
            RegisterInfoFragment::class.java.name ->{
                RegisterInfoFragment(viewModelFactory)
            }


            else -> {
                LoginFragment(viewModelFactory)
            }
        }


}