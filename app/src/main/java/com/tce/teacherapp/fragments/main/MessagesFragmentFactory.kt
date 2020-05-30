package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.messages.MessageListFragment
import javax.inject.Inject

class MessagesFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            MessageListFragment::class.java.name -> {
                MessageListFragment(viewModelFactory)
            }

            else -> {
                MessageListFragment(viewModelFactory)
            }
        }


}