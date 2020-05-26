package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import com.tce.teacherapp.ui.UICommunicationListener
import com.tce.teacherapp.ui.dashboard.subjects.SubjectListFragment
import com.tce.teacherapp.viewmodels.FakeMainViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Singleton
class FakeMainFragmentFactory
@Inject
constructor(
    private val viewModelFactory: FakeMainViewModelFactory
): FragmentFactory(){

    // used for setting a mock<UICommunicationListener>
    lateinit var uiCommunicationListener: UICommunicationListener

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when(className){

            SubjectListFragment::class.java.name -> {
                val fragment = SubjectListFragment(viewModelFactory)
                if(::uiCommunicationListener.isInitialized){
                    fragment.setUICommunicationListener(uiCommunicationListener)
                }
                fragment
            }


            else -> {
                super.instantiate(classLoader, className)
            }
        }
}








