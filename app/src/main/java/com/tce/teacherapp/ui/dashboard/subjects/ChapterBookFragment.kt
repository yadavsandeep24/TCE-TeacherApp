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
import com.tce.teacherapp.databinding.FragmentSelectChapterBookBinding
import com.tce.teacherapp.db.entity.Topic
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.adapter.chapterListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.state.SUBJECT_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.StateMessageCallback
import com.tce.teacherapp.util.Utility
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class ChapterBookFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_select_chapter_book,viewModelFactory) {

    private lateinit var binding: FragmentSelectChapterBookBinding

    var topicVo: Topic? = null
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

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        outState.putParcelable(SUBJECT_VIEW_STATE_BUNDLE_KEY, viewState)
        super.onSaveInstanceState(outState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectChapterBookBinding.inflate(inflater, container, false)
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
        (activity as DashboardActivity).showHideBottomBar(true)

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
        tvTopicTitle.text = topicVo!!.label

        val searchText: TextView = binding.svChapterBook.findViewById(R.id.search_src_text) as TextView
        val myCustomFont: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Rubik-Medium.ttf")
        searchText.typeface = myCustomFont
        searchText.textSize = requireActivity().resources.getDimension(R.dimen.font_size_14_px)

        val searchIcon = binding.svChapterBook.findViewById(R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.search)
        binding.svChapterBook.setOnQueryTextListener(queryTextListener)
        // Get the search close button image view
        val closeButton: ImageView =
            binding.svChapterBook.findViewById(R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
            val t: Toast = Toast.makeText(activity, "close", Toast.LENGTH_SHORT)
            t.show()
            uiCommunicationListener.hideSoftKeyboard()
            binding.svChapterBook.setQuery("", false)
            binding.svChapterBook.clearFocus()
            if (topicVo != null) {
                viewModel.setStateEvent(SubjectStateEvent.GetChapterEvent("", topicVo!!.id,topicVo!!.bookId))
            }
            false
        }

        if (topicVo != null) {
            viewModel.setStateEvent(SubjectStateEvent.GetChapterEvent("", topicVo!!.id,topicVo!!.bookId))
        }

        binding.rvChapterBook.layoutManager = GridLayoutManager(activity, 2)
        binding.rvChapterBook.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvChapterBook)
        binding.rvChapterBook.addGlidePreloader(
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
            if (topicVo != null) {
                viewModel.setStateEvent(SubjectStateEvent.GetChapterEvent(newText, topicVo!!.id,topicVo!!.bookId))
            }
            return true

        }
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.chapterLearnData?.let {
                    binding.rvChapterBook.withModels {
                        for (chapter in it.chapterList!!) {
                            chapterListEpoxyHolder {
                                id(chapter.id)
                                title(chapter.label)
                                chapter.image?.let { it1 -> imageUrl(it1) }
                                try {
                                    var name = chapter.icon?.substring(0, chapter.icon!!.lastIndexOf("."))
                                        ?.replace("-","_")
                                    if(chapter.icon.isNullOrEmpty()){
                                        name ="default_chapter"
                                    }
                                    Log.d("SAN", "name-->$name")
                                    if(Utility.getDrawable(name?.toLowerCase(Locale.ROOT),
                                            requireContext()) == null){
                                        Utility.getDrawable("default_chapter", requireContext())?.let { it1 -> imageDrawable(it1) }
                                    }else {
                                        Utility.getDrawable(
                                            name?.toLowerCase(Locale.ROOT),
                                            requireContext()
                                        )?.let { it1 -> imageDrawable(it1) }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                listener {
                                    val bundle = Bundle()
                                    bundle.putParcelable("chapterdata", chapter)
                                    findNavController().navigate(R.id.action_selectChapterBookFragment_to_chapterResourceSelectionFragment,bundle)

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