package com.tce.teacherapp.ui.dashboard.messages

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker

import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentResourceBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.resourceEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.adapter.resourceTypeEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class ResourceFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseMessageFragment(R.layout.fragment_resource, viewModelFactory) {

    private lateinit var binding : FragmentResourceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "MessageViewState: inState is NOT null")
            (inState[MESSAGE_VIEW_STATE_BUNDLE_KEY] as MessageViewState?)?.let { viewState ->
                Log.d(TAG, "MessageViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.

        viewState?.resourceList = ArrayList()
        viewState?.selectedResourceList = ArrayList()

        outState.putParcelable(
            MESSAGE_VIEW_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResourceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)

        viewModel.setStateEvent(MessageStateEvent.GetResourceEvent(""))

        val searchText: TextView =
            binding.svResources.findViewById(R.id.search_src_text) as TextView
        val myCustomFont: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Rubik-Medium.ttf")
        searchText.typeface = myCustomFont
        searchText.textSize = requireActivity().resources.getDimension(R.dimen.font_size_14_px)

        val searchIcon = binding.svResources.findViewById(R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.search)

        binding.svResources.setOnQueryTextListener(queryTextListener)
        // Get the search close button image view
        val closeButton: ImageView =
            binding.svResources.findViewById(R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
            val t: Toast = Toast.makeText(activity, "close", Toast.LENGTH_SHORT)
            t.show()
            uiCommunicationListener.hideSoftKeyboard()
            binding.svResources.setQuery("", false)
            binding.svResources.clearFocus()
            viewModel.setStateEvent(MessageStateEvent.GetResourceEvent(""))
            false
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvResourceType.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)

        }

        binding.imgBack.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })

        binding.rvResource.layoutManager = GridLayoutManager(activity, 1)
        binding.rvResource.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvResource)
        subscribeObservers()

    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            viewModel.setStateEvent(MessageStateEvent.GetResourceEvent(newText))
            return true

        }
    }


    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.resourceList?.let {
                    Log.d("SAN", "messageList-->" + it.size)
                    binding.rvResourceType.withModels {
                        for (res in it) {
                            resourceTypeEpoxyHolder {
                                id(res.id.toLong())
                                strType(res.Type)
                                listener {
                                    viewModel.setStateEvent(MessageStateEvent.GetResourceSelectionEvent("",res.typeId))
                                   requestModelBuild()

                                }
                            }
                        }

                    }
                }

                viewState.resourceList?.let {
                    binding.rvResource.withModels {
                        for (resource in it!!) {
                            resourceEpoxyHolder {
                                id(resource.id.toLong())
                                strTitle(resource.title)
                                listener {

                                }
                            }
                        }
                    }
                }


                viewState.selectedResourceList?.let {
                    binding.rvResource.withModels {
                        for (resource in it!!) {
                            resourceEpoxyHolder {
                                id(resource.id.toLong())
                                strTitle(resource.title)
                                listener {

                                }
                            }
                        }
                    }
                }

                binding.tvAll.setOnClickListener(View.OnClickListener {
                    viewState.resourceList?.let {
                        binding.rvResource.withModels {
                            for (resource in it!!) {
                                resourceEpoxyHolder {
                                    id(resource.id.toLong())
                                    strTitle(resource.title)
                                    listener {

                                    }
                                }
                            }
                        }
                    }
                })
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "SubjectListFragment-->viewModel.stateMessage")

            stateMessage?.let {

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })



    }

}
