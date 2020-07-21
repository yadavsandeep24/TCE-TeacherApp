package com.tce.teacherapp.ui.dashboard.subjects

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.ChapterResourceSelectionActivityBinding
import com.tce.teacherapp.db.entity.Chapter
import com.tce.teacherapp.db.entity.ChapterResourceType
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
import com.tce.teacherapp.util.StateMessageCallback
import com.tce.teacherapp.util.Utility
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class ChapterResourceSelectionFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.chapter_resource_selection_activity, viewModelFactory) {

    lateinit var binding: ChapterResourceSelectionActivityBinding

    var chapterVo: Chapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        binding = ChapterResourceSelectionActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chapterVo = arguments?.getParcelable("chapterdata") as Chapter?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        Glide.with(requireActivity())
            .load(R.drawable.group)
            .transform(BlurTransformation(200, 2))
            .into(binding.ivContainer)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)
        (activity as DashboardActivity).showHideBottomBar(false)

        binding.ivCancel.setOnClickListener {
            activity?.onBackPressed()
        }
        viewModel.setStateEvent(
            SubjectStateEvent.GetChapterResourceTypeEvent(
                chapterVo!!.id,
                chapterVo!!.topicId,
                chapterVo!!.bookId
            )
        )
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.chapterResourceTyeList?.let {
                    setResourceItem(requireContext().resources.configuration.orientation, it)
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

    private fun setResourceItem(
        orientation: Int,
        it: List<ChapterResourceType>
    ) {
        binding.llRow1.removeAllViews()
        binding.llRow2.removeAllViews()
        binding.llRow3.removeAllViews()
        binding.llRow4.removeAllViews()
        binding.llRow5.removeAllViews()
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.llRow4.visibility = View.VISIBLE
            binding.llRow5.visibility = View.VISIBLE
            val chunked = it.chunked(3)

            for (j in chunked.indices) {
                val chunkedResourceTypeList = chunked[j]
                val colSize = chunkedResourceTypeList.size
                for (i in chunkedResourceTypeList.indices) {
                    if (colSize == 1) {
                        setResourceCell(getParentView(j), chunkedResourceTypeList[i], 3)
                    } else if (colSize == 2) {
                        if (i == 0) {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                        } else {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                        }
                    } else if (colSize == 3) {
                        when (i) {
                            0 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                            }
                            1 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], -1)
                            }
                            2 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                            }
                        }
                    }
                }
            }
        } else {
            binding.llRow4.visibility = View.GONE
            binding.llRow5.visibility = View.GONE
            val chunked = it.chunked(5)

            for (j in chunked.indices) {
                val chunkedResourceTypeList = chunked[j]
                val colSize = chunkedResourceTypeList.size
                for (i in chunkedResourceTypeList.indices) {
                    if (colSize == 1) {
                        setResourceCell(getParentView(j), chunkedResourceTypeList[i], 3)
                    } else if (colSize == 2) {
                        if (i == 0) {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                        } else {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                        }
                    } else {
                        when (i) {
                            0 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                            }
                            colSize - 1 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                            }
                            else -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], -1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getParentView(position: Int): LinearLayout {
        return when (position) {
            0 -> {
                binding.llRow1
            }
            1 -> {
                binding.llRow2
            }
            2 -> {
                binding.llRow3
            }
            3 -> {
                binding.llRow4
            }
            4 -> {
                binding.llRow5
            }
            else -> {
                binding.llRow1
            }
        }
    }

    private fun setResourceCell(
        parentView: LinearLayout,
        chapterResourceType: ChapterResourceType,
        viewType: Int
    ) {
        var view = LayoutInflater.from(activity)
            .inflate(R.layout.resource_selection_item_center, parentView, false)
        if (viewType == 1) {
            view = LayoutInflater.from(activity)
                .inflate(R.layout.resource_selection_item_left, parentView, false)
        } else if (viewType == 2) {
            view = LayoutInflater.from(activity)
                .inflate(R.layout.resource_selection_item_right, parentView, false)
        } else if (viewType == 3) {
            view = LayoutInflater.from(activity)
                .inflate(R.layout.resource_selection_item_left_right, parentView, false)
        }
        val ivIcon = view.findViewById<AppCompatImageView>(R.id.iv_icon)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        var iconDrawable : Drawable? = null
        if(chapterResourceType.icon != null && chapterResourceType.icon!!.isNotEmpty()) {
            iconDrawable = Utility.getDrawable((chapterResourceType.icon?.substring(0,chapterResourceType.icon!!.lastIndexOf(".")))?.replace("-","_"), requireContext())
        }
        if (iconDrawable != null) {
            ivIcon.setBackgroundDrawable(iconDrawable)
        } else {
            ivIcon.setBackgroundDrawable(resources.getDrawable(R.drawable.music_time))
        }
        tvName.text = chapterResourceType.label
        parentView.addView(view)
        view.tag =
            chapterResourceType.id + "/" + chapterResourceType.chapterId + "/" + chapterResourceType.topicId + "/" + chapterResourceType.bookId
        setListener(view,chapterResourceType)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.setStateEvent(
            SubjectStateEvent.GetChapterResourceTypeEvent(
                chapterVo!!.id,
                chapterVo!!.topicId,
                chapterVo!!.bookId
            )
        )
    }

    private fun setListener(view: View, chapterResourceType: ChapterResourceType) {
        view.setOnClickListener {
            if(chapterResourceType.resourceList.isNotEmpty()){
                val bundle = Bundle()
                if(chapterResourceType.resourceList.size>1) {
                    bundle.putString("parent1Id", chapterResourceType.id)
                    bundle.putString("parent2Id", chapterResourceType.chapterId)
                    if(chapterResourceType.id.equals("resource",true)) {
                        bundle.putString("parent1Id", chapterResourceType.topicId)
                        bundle.putString("parent2Id", chapterResourceType.topicId)
                    }
                    findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_resourceListFragment, bundle)
                }else {
                    val resource = chapterResourceType.resourceList[0]
                    bundle.putString("title",resource.title)
                    bundle.putString("url",resource.src)
                    when {
                        resource.contenttype.equals("av", true) -> {
                            bundle.putBoolean("isModality",true)
                            findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_videoPlayerFragment,bundle)
                        }
                        resource.contenttype.equals("Image", true) -> {
                            findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_imageContentFragment,bundle)
                        }
                        resource.contenttype.equals("activity", true) -> {
                            findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_HTMLContentFragment,bundle)
                        }
                        resource.contenttype.equals("audio", true) -> {
                        }
                    }
                }
            }

        }
    }
}