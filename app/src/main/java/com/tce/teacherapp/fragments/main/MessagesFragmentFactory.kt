package com.tce.teacherapp.fragments.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.ui.dashboard.messages.*
import com.tce.teacherapp.ui.dashboard.subjects.HTMLContentFragment
import com.tce.teacherapp.ui.dashboard.subjects.ImageContentFragment
import com.tce.teacherapp.ui.dashboard.subjects.VideoPlayerFragment
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
                GroupChatFragment(viewModelFactory)
            }

            ResourceFragment::class.java.name -> {
                ResourceFragment(viewModelFactory)
            }

            GroupInfoFragment::class.java.name -> {
                GroupInfoFragment(viewModelFactory)
            }
            ImageContentFragment::class.java.name ->{
                ImageContentFragment(viewModelFactory)
            }
            VideoPlayerFragment::class.java.name -> {
                VideoPlayerFragment(viewModelFactory)
            }
            HTMLContentFragment::class.java.name -> {
                HTMLContentFragment(viewModelFactory)
            }

            else -> {
                MessageListFragment(viewModelFactory)
            }
        }


}