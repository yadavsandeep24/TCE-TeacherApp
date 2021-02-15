package com.tce.teacherapp.ui.dashboard.subjects

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentResourceListBinding
import com.tce.teacherapp.db.entity.Resource
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.subjects.adapter.topicResourceEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class ResourceListFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_resource_list,viewModelFactory) {
    private lateinit var binding: FragmentResourceListBinding
    private var  chapterLabel:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        binding = FragmentResourceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parent1Id = arguments?.getString("parent1Id")
        val parent2Id = arguments?.getString("parent2Id")
        chapterLabel = arguments?.getString("chapterLabel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideBottomBar(false)
        Glide.with(requireActivity())
            .load(R.drawable.group)
            .into(binding.ivContainer)
        viewModel.setStateEvent(SubjectStateEvent.GetTopicResourceEvent("", parent1Id!!,parent2Id!!))

        binding.tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
        subscribeObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.chapterLearnData?.let {

                    if (it.resourceList != null) {
                        binding.rvChapterLearnResource.withModels {
                            val countValue =it.resourceList!!.size
                            binding.tvResourceCount.text = "$countValue files found"
                            for (resource in it.resourceList!!) {
                                topicResourceEpoxyHolder {
                                    id(resource.id)
                                    resourceVo(resource)
                                    listener {
                                        val bundle = Bundle()
                                        val allTopicResourceList:ArrayList<Resource>? = arguments?.getParcelableArrayList("resourceList")
                                        bundle.putParcelableArrayList("resourceList",allTopicResourceList)
                                        bundle.putParcelable("resourceVo",resource)
                                        if(chapterLabel.isNullOrEmpty()) {
                                            bundle.putString("title", resource.title)
                                        }else{
                                            bundle.putString("title", chapterLabel+"—"+resource.title)
                                        }
                                        bundle.putString("url",resource.src)
                                        when {
                                            resource.contenttype.equals("av", true) -> {
                                                bundle.putBoolean("isModality",true)
                                                findNavController().navigate(R.id.action_resourceListFragment_to_videoPlayerFragment,bundle)
                                            }
                                            resource.contenttype.equals("Image", true) -> {
                                                findNavController().navigate(R.id.action_resourceListFragment_to_imageContentFragment,bundle)
                                            }
                                            resource.contenttype.equals("activity", true) -> {
                                                findNavController().navigate(R.id.action_resourceListFragment_to_HTMLContentFragment,bundle)
                                            }
                                            resource.contenttype.equals("audio", true) -> {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            Log.d("SAN", "viewModel.areAnyJobsActive()-->" + viewModel.areAnyJobsActive())
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