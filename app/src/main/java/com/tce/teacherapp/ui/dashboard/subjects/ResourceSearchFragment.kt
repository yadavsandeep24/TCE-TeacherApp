package com.tce.teacherapp.ui.dashboard.subjects

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentResourceSearchBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.subjects.adapter.OptionListAdapter
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
import com.tce.teacherapp.util.StateMessageCallback
import com.tce.teacherapp.util.sheets.RightSheetBehavior
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class ResourceSearchFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_resource_search, viewModelFactory) {

    private lateinit var binding: FragmentResourceSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentResourceSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideBottomBar(false)

        val searchKeyWords :  MutableList<String> = mutableListOf()
        searchKeyWords.add("Zoo")
        searchKeyWords.add("Animals")
        searchKeyWords.add("Bear")
        searchKeyWords.add("Car")

        binding.svResourceSearch.setOnQueryTextListener(queryTextListener)
        binding.svResourceSearch.setIconifiedByDefault(false)

        if(!searchKeyWords.isNullOrEmpty()) {
            binding.lnrSearchKeywords.visibility = View.VISIBLE
            for (searchKey in searchKeyWords) {
                val skillView = LayoutInflater.from(context).inflate(
                    R.layout.list_item_skills,
                    binding.lnrSearchKeywords,
                    false
                )
                val tvName = skillView.findViewById(R.id.tv_skill_name) as TextView
                val container = skillView.findViewById(R.id.container) as LinearLayout
                tvName.text = searchKey
                container.setPadding(15, 0, 10, 0)
                binding.lnrSearchKeywords.addView(skillView)
            }
        }

        val rightSheetBehavior = RightSheetBehavior.from(binding.rightSheet)
        val rightSheetSelectionBehavior = RightSheetBehavior.from(binding.rightSheetSelection)

        rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
        rightSheetBehavior.skipCollapsed = true
        rightSheetBehavior.isDraggable = false


        rightSheetSelectionBehavior.state = RightSheetBehavior.STATE_HIDDEN
        rightSheetSelectionBehavior.skipCollapsed = true
        rightSheetSelectionBehavior.isDraggable = false


        rightSheetBehavior.addRightSheetCallback(object :
            RightSheetBehavior.RightSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING) {
                    rightSheetBehavior.state = RightSheetBehavior.STATE_EXPANDED
                }

            }

            override fun onSlide(rightSheet: View, slideOffset: Float) {
            }
        })

        rightSheetSelectionBehavior.addRightSheetCallback(object :
            RightSheetBehavior.RightSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING) {
                    rightSheetSelectionBehavior.state = RightSheetBehavior.STATE_EXPANDED
                }

            }

            override fun onSlide(rightSheet: View, slideOffset: Float) {
            }
        })

        binding.lblClearAll.paintFlags = binding.lblClearAll.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.maskLayout.setOnClickListener {
            rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }

        binding.vwClose.setOnClickListener {
            rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }

        binding.vwSort.setOnClickListener {
            if (rightSheetBehavior.state == RightSheetBehavior.STATE_HIDDEN) {
                rightSheetBehavior.state = RightSheetBehavior.STATE_EXPANDED
                uiCommunicationListener.hideSoftKeyboard()
                binding.maskLayout.visibility = View.VISIBLE
                viewModel.setStateEvent(SubjectStateEvent.GetDivisionEvent)
            } else {
                rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }

        binding.vwArrowRightYear.setOnClickListener {
            binding.tvTitle.text = "Year"
            rightSheetSelectionBehavior.state = RightSheetBehavior.STATE_EXPANDED
            rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
            uiCommunicationListener.hideSoftKeyboard()
            viewModel.setStateEvent(SubjectStateEvent.GetYearOptionListEvent)
        }

        binding.vwArrowRightSubjects.setOnClickListener {
            binding.tvTitle.text = "Subjects"
            rightSheetSelectionBehavior.state = RightSheetBehavior.STATE_EXPANDED
            rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
            uiCommunicationListener.hideSoftKeyboard()
            viewModel.setStateEvent(SubjectStateEvent.GetSubjectEvent(""))
        }


        binding.vwArrowRightBooks.setOnClickListener {
            binding.tvTitle.text = "Books"
            uiCommunicationListener.hideSoftKeyboard()
            RightSheetBehavior.from(binding.rightSheet).state = RightSheetBehavior.STATE_HIDDEN
            viewModel.setStateEvent(SubjectStateEvent.GetBookEvent("","00"))
        }

        binding.vwArrowRightTopic.setOnClickListener {
            binding.tvTitle.text = "Topic"
            rightSheetSelectionBehavior.state = RightSheetBehavior.STATE_EXPANDED
            rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
            uiCommunicationListener.hideSoftKeyboard()
            viewModel.setStateEvent(SubjectStateEvent.GetChapterEvent("", "", ""
                )
            )
        }

        binding.vwArrowRightMedia.setOnClickListener {
            binding.tvTitle.text = "Media"
            rightSheetSelectionBehavior.state = RightSheetBehavior.STATE_EXPANDED
            rightSheetBehavior.state = RightSheetBehavior.STATE_HIDDEN
            uiCommunicationListener.hideSoftKeyboard()
            val adapter = binding.rvOptionList.adapter as OptionListAdapter
            adapter.setData(searchKeyWords)
        }

        binding.imgBack.setOnClickListener {
            rightSheetSelectionBehavior.state = RightSheetBehavior.STATE_HIDDEN
            rightSheetBehavior.state = RightSheetBehavior.STATE_EXPANDED

        }

        binding.rvOptionList.layoutManager = GridLayoutManager(activity, 1)
        binding.rvOptionList.setHasFixedSize(true)
        val optionListAdapter = OptionListAdapter(requireContext())
        binding.rvOptionList.adapter = optionListAdapter

        binding.tvLatestDate.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            binding.tvLatestDate.background = resources.getDrawable(R.drawable.ic_rectangle)
            binding.tvLatestDate.setTextColor(resources.getColor(R.color.deep_brown))
            binding.tvAlphabetical.background =
                resources.getDrawable(R.drawable.ic_rectangle_unselected)
            binding.tvAlphabetical.setTextColor(resources.getColor(R.color.dark))
        }

        binding.tvAlphabetical.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            binding.tvAlphabetical.background = resources.getDrawable(R.drawable.ic_rectangle)
            binding.tvAlphabetical.setTextColor(resources.getColor(R.color.deep_brown))
            binding.tvLatestDate.background = resources.getDrawable(R.drawable.ic_rectangle_unselected)
            binding.tvLatestDate.setTextColor(resources.getColor(R.color.dark))
        }

        subscribeObservers()
    }
    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.gradeList?.let {
                    binding.lblValueYear.text = "("+it[0].gradeTitle+")"
                    val adapter = binding.rvOptionList.adapter as OptionListAdapter
                    adapter.setData(it)
                }
                viewState.subjectList?.let {
                    val adapter = binding.rvOptionList.adapter as OptionListAdapter
                    adapter.setData(it)
                }
                viewState.topicList?.let {
                    RightSheetBehavior.from(binding.rightSheetSelection).state = RightSheetBehavior.STATE_EXPANDED

                    val adapter = binding.rvOptionList.adapter as OptionListAdapter
                    adapter.setData(it)
                }


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

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            val closeButton: ImageView = binding.svResourceSearch.findViewById(R.id.search_close_btn) as ImageView
            closeButton.visibility = GONE
            return true

        }
    }

}