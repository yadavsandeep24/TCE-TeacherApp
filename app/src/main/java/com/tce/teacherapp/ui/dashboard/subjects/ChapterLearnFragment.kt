package com.tce.teacherapp.ui.dashboard.subjects

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentChapterLearnBinding
import com.tce.teacherapp.db.entity.Topic
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.adapter.chapterListLearnEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.adapter.topicResourceEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.state.SUBJECT_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.StateMessageCallback
import com.tce.teacherapp.util.Utility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class ChapterLearnFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_chapter_learn, viewModelFactory) {

    private lateinit var binding: FragmentChapterLearnBinding

    var topicVo: Topic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SubjectViewState: inState is NOT null")
        savedInstanceState?.let { inState ->
            (inState[SUBJECT_VIEW_STATE_BUNDLE_KEY] as SubjectViewState?)?.let { viewState ->
                Log.d(TAG, "SubjectViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChapterLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicVo = arguments?.getParcelable("topicdata") as Topic?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        val topBar = (activity as DashboardActivity).binding.toolBar.findViewById<RelativeLayout>(R.id.top_container)
        topBar.setBackgroundColor(resources.getColor(R.color.subject_actionbar_color))

        val spnDivision = (activity as DashboardActivity).binding.toolBar.findViewById<AppCompatSpinner>(R.id.spn_division)
        spnDivision.visibility = View.GONE

        val tvBack = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE

        val tvTopicTitle =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)

        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
        tvTopicTitle.text = topicVo?.label ?: ""

        val searchText: TextView =
            binding.svChapterLearn.findViewById(R.id.search_src_text) as TextView
        val myCustomFont: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Rubik-Medium.ttf")
        searchText.typeface = myCustomFont
        searchText.textSize = requireActivity().resources.getDimension(R.dimen.font_size_14_px)

        val searchIcon = binding.svChapterLearn.findViewById(R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.search)
        binding.svChapterLearn.setOnQueryTextListener(queryTextListener)
        // Get the search close button image view
        val closeButton: ImageView =
            binding.svChapterLearn.findViewById(R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
            val t: Toast = Toast.makeText(activity, "close", Toast.LENGTH_SHORT)
            t.show()
            uiCommunicationListener.hideSoftKeyboard()
            binding.svChapterLearn.setQuery("", false)
            binding.svChapterLearn.clearFocus()
            if (topicVo != null) {
                viewModel.setStateEvent(
                    SubjectStateEvent.GetChapterEvent(
                        "",
                        topicVo!!.id,
                        topicVo!!.bookId
                    )
                )
            }
            false
        }

        if (topicVo != null) {
            viewModel.setStateEvent(
                SubjectStateEvent.GetChapterEvent(
                    "",
                    topicVo!!.id,
                    topicVo!!.bookId
                )
            )
        }
        binding.rvChapterLearn.layoutManager = GridLayoutManager(activity, 3)
        binding.rvChapterLearn.setHasFixedSize(true)
        var epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvChapterLearn)
        binding.rvChapterLearn.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, model: SubjectListEpoxyHolder, _ ->
                requestManager.loadImage(model.imageUrl)
            }
        )

        binding.rvChapterLearnResource.layoutManager = GridLayoutManager(activity, 1)
        binding.rvChapterLearnResource.setHasFixedSize(true)
        epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvChapterLearnResource)
        binding.rvChapterLearnResource.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, model: SubjectListEpoxyHolder, _ ->
                requestManager.loadImage(model.imageUrl)
            }
        )



        binding.tvLearn.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            binding.rvChapterLearn.visibility = View.VISIBLE
            binding.llResourceContainer.visibility = View.GONE
            binding.tvLearn.background = resources.getDrawable(R.drawable.ic_rectangle)
            binding.tvLearn.setTextColor(resources.getColor(R.color.deep_brown))
            binding.tvResource.background =
                resources.getDrawable(R.drawable.ic_rectangle_unselected)
            binding.tvResource.setTextColor(resources.getColor(R.color.dark))
            viewModel.setStateEvent(SubjectStateEvent.GetChapterEvent("",topicVo!!.id,topicVo!!.bookId))
        }

        binding.tvResource.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            binding.rvChapterLearn.visibility = View.GONE
            binding.llResourceContainer.visibility = View.VISIBLE
            binding.tvResource.background = resources.getDrawable(R.drawable.ic_rectangle)
            binding.tvResource.setTextColor(resources.getColor(R.color.deep_brown))
            binding.tvLearn.background = resources.getDrawable(R.drawable.ic_rectangle_unselected)
            binding.tvLearn.setTextColor(resources.getColor(R.color.dark))
            viewModel.setStateEvent(SubjectStateEvent.GetTopicResourceEvent("", topicVo!!.id))
        }
        subscribeObservers()
    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            if (topicVo != null) {
                viewModel.setStateEvent(
                    SubjectStateEvent.GetChapterEvent(
                        newText,
                        topicVo!!.id,
                        topicVo!!.bookId
                    )
                )
            }
            return true

        }
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.chapterLearnData?.let {

                    if (it.chapterList != null) {
                        binding.rvChapterLearn.withModels {
                            for (chapter in it.chapterList!!) {
                                chapterListLearnEpoxyHolder {
                                    id(chapter.id)
                                    chapter.image?.let { it1 -> imageUrl(it1) }
                                    try {
                                        Utility.getDrawable("ic_b", requireContext())
                                            ?.let { it1 -> imageDrawable(it1) }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    listener {
                                        val bundle = Bundle()
                                      //  bundle.putParcelable("chapterdata", chapter)
                                        val i = Intent(context,ChapterResourceSelectionActivity::class.java)
                                        startActivity(i)
                                    }
                                }
                            }
                        }
                    }

                    if (it.resourceList != null) {
                        binding.rvChapterLearnResource.withModels {
                            for (resource in it.resourceList!!) {
                                topicResourceEpoxyHolder {
                                    id(resource.id)
                                    listener {

                                    }
                                }
                            }
                        }
                    }
                }


//                viewState.chapterList?.let { chapterList ->
//                    viewState.chapterList = emptyList()
//                    binding.rvChapterLearn.visibility = View.VISIBLE
//                    binding.rvChapterLearn.layoutManager = GridLayoutManager(activity, 3)
//
//                    binding.rvChapterLearn.withModels {
//                        for (chapter in chapterList) {
//                            chapterListLearnEpoxyHolder {
//                                id(chapter.id)
//                                chapter.image?.let { it1 -> imageUrl(it1) }
//                                try {
//                                    Utility.getDrawable("ic_b", requireContext())
//                                        ?.let { it1 -> imageDrawable(it1) }
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                                listener {
//                                }
//                            }
//                        }
//
//                    }
//                }
//                viewState.resourceList?.let { resourceList ->
//                    viewState.resourceList = emptyList()
//                    binding.rvChapterLearn.visibility = View.VISIBLE
//                    binding.rvChapterLearn.layoutManager = GridLayoutManager(activity, 1)
//                    binding.rvChapterLearn.withModels {
//                        for (resource in resourceList) {
//                            topicResourceEpoxyHolder {
//                                id(resource.id)
//                                listener {
//
//                                }
//                            }
//                        }
//
//                    }
//                }
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