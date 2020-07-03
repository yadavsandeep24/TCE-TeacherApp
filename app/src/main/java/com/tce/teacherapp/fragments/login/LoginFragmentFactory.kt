package com.tce.teacherapp.fragments.login

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
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

            MobileNumberEnterFragment::class.java.name ->{
                MobileNumberEnterFragment(viewModelFactory)
            }

            VerifyOTPFragment::class.java.name ->{
                VerifyOTPFragment(viewModelFactory)
            }

            NewPasswordCreateFragment::class.java.name ->{
                NewPasswordCreateFragment(viewModelFactory)
            }


            else -> {
                LoginFragment(viewModelFactory)
            }
        }


}