package com.tce.teacherapp.ui.dashboard.subjects

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentTopicListBinding
import com.tce.teacherapp.db.entity.Subject
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.adapter.topicListEpoxyHolder
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
class TopicListFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_topic_list,viewModelFactory) {

    private lateinit var binding: FragmentTopicListBinding

    var subjectVo: Subject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "SubjectViewState: inState is NOT null")
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
        binding = FragmentTopicListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.topicList = ArrayList()
        outState.putParcelable(SUBJECT_VIEW_STATE_BUNDLE_KEY, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subjectVo = arguments?.getParcelable("subjectdata") as Subject?
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

        val spnDivision =  (activity as DashboardActivity).binding.toolBar.findViewById<AppCompatSpinner>(R.id.spn_division)
        spnDivision.visibility = View.GONE

        val tvBack = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE

        val tvTopicTitle = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)

        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
        tvTopicTitle.text = subjectVo!!.title

        val searchText: TextView = binding.svTopic.findViewById(R.id.search_src_text) as TextView
        val myCustomFont: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Rubik-Medium.ttf")
        searchText.typeface = myCustomFont
        searchText.textSize = requireActivity().resources.getDimension(R.dimen.font_size_14_px)

        val searchIcon = binding.svTopic.findViewById(R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.search)
        binding.svTopic.setOnQueryTextListener(queryTextListener)
        // Get the search close button image view
        val closeButton: ImageView =
            binding.svTopic.findViewById(R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
            val t: Toast = Toast.makeText(activity, "close", Toast.LENGTH_SHORT)
            t.show()
            uiCommunicationListener.hideSoftKeyboard()
            binding.svTopic.setQuery("", false)
            binding.svTopic.clearFocus()
            if (subjectVo != null) {
                viewModel.setStateEvent(SubjectStateEvent.GetTopicEvent("", subjectVo!!.id))
            }
            false
        }

        if (subjectVo != null) {
            viewModel.setStateEvent(SubjectStateEvent.GetTopicEvent("", subjectVo!!.id))
        }

        binding.rvTopic.layoutManager = GridLayoutManager(activity, 2)
        binding.rvTopic.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvTopic)
        binding.rvTopic.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, model: SubjectListEpoxyHolder, _ ->
                requestManager.loadImage(model.imageUrl)
            }
        )
        subscribeObservers()
    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            if (subjectVo != null) {
                viewModel.setStateEvent(SubjectStateEvent.GetTopicEvent(newText, subjectVo!!.id))
            }
            return true

        }
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.topicList?.let {
                    Log.d("SAN", "topic-->" + it.size)
                    binding.rvTopic.withModels {
                        for (topic in it) {
                            topicListEpoxyHolder {
                                id(topic.id)
                                title(topic.label)
                                imageUrl(topic.label)
                                Utility.getDrawable("topic_" + topic.index, requireContext())
                                    ?.let { it1 -> imageDrawable(it1) }
                                listener {
                                    topic.section?.let { sectionType ->
                                        val bundle = Bundle()
                                        bundle.putParcelable("topicdata", topic)
                                        if(sectionType.equals("learn",true)) {
                                            findNavController().navigate(R.id.action_topicFragment_to_selectChapterLearnFragment)
                                        }else if(sectionType.equals("book",true)){
                                            findNavController().navigate(R.id.action_topicFragment_to_selectChapterBookFragment,bundle)
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
            Log.d("SAN","viewModel.areAnyJobsActive()-->"+viewModel.areAnyJobsActive())
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
