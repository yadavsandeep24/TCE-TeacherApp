package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.messages.*
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

            MessageDetailFragment::class.java.name -> {
                MessageDetailFragment(viewModelFactory)
            }

            NewMessageFragment::class.java.name -> {
                NewMessageFragment(viewModelFactory)
            }

            GroupChatFragment::class.java.name -> {
                GroupChatFragment()
            }

            ResourceFragment::class.java.name -> {
                ResourceFragment(viewModelFactory)
            }

            GroupInfoFragment::class.java.name -> {
                GroupInfoFragment(viewModelFactory)
            }

            else -> {
                MessageListFragment(viewModelFactory)
            }
        }


}