package com.tce.teacherapp.ui.dashboard.subjects

import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
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
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentSubjectListBinding
import com.tce.teacherapp.fragments.main.CustomSpinnerAdapter
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.adapter.subjectListEpoxyHolder
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
class SubjectListFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_subject_list,viewModelFactory) {

    private lateinit var binding: FragmentSubjectListBinding
    private lateinit var spnDivision: AppCompatSpinner

    var adapter: CustomSpinnerAdapter? = null
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.gradeList = ArrayList()
        viewState?.subjectList = ArrayList()


        outState.putParcelable(
            SUBJECT_VIEW_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        spnDivision =  (activity as DashboardActivity).binding.toolBar.findViewById(R.id.spn_division)
        ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_back) as TextView).visibility = View.GONE

        uiCommunicationListener.displayProgressBar(true)
        val searchText: TextView = binding.svSubjects.findViewById(R.id.search_src_text) as TextView
        val myCustomFont: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Rubik-Medium.ttf")
        searchText.typeface = myCustomFont
        searchText.textSize = requireActivity().resources.getDimension(R.dimen.font_size_14_px)

        val searchIcon = binding.svSubjects.findViewById(R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.search)


        binding.svSubjects.setOnQueryTextListener(queryTextListener)
        // Get the search close button image view
        val closeButton: ImageView =
            binding.svSubjects.findViewById(R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
            val t: Toast = Toast.makeText(activity, "close", Toast.LENGTH_SHORT)
            t.show()
            uiCommunicationListener.hideSoftKeyboard()
            binding.svSubjects.setQuery("", false)
            binding.svSubjects.clearFocus()
            viewModel.setStateEvent(SubjectStateEvent.GetSubjectEvent(""))
            false
        }

        viewModel.setStateEvent(SubjectStateEvent.GetDivisionEvent)

        binding.rvSubjects.layoutManager = GridLayoutManager(activity, 2)
        binding.rvSubjects.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvSubjects)
        binding.rvSubjects.addGlidePreloader(
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
            viewModel.setStateEvent(SubjectStateEvent.GetSubjectEvent(newText))
            return true

        }
    }

    class SpinnerInteractionListener(
        private val adapter: CustomSpinnerAdapter,
        private val viewModel: SubjectsViewModel
    ) : OnItemSelectedListener, OnTouchListener {
        private var userSelect = false
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v != null) {
                userSelect = true
            }
            return false
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (parent != null) {
                if (userSelect) {
                    Log.d("SAN", "onItemSelected-->$position")
                    val selectedGrade = adapter?.getItem(position)
                    selectedGrade?.let {
                        viewModel.setStateEvent(
                            SubjectStateEvent.DivisionSelectionEvent(
                                it,
                                position
                            )
                        )
                    }
                    userSelect = false
                }
            }
        }
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.gradeList?.let {
                    ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_back) as TextView).visibility = View.GONE
                    val tvTopicTitle = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)
                    tvTopicTitle.text = resources.getString(R.string.title_subjects)
                    spnDivision.visibility = View.VISIBLE


                    Log.d("SAN", "gradeList-->" + it.size)
                    adapter = activity?.let { it1 ->
                        CustomSpinnerAdapter(it1, R.layout.spinner_dropdown, R.id.text1, it)
                    }
                    spnDivision.adapter = adapter

                    val listener =
                        adapter?.let { it1 -> SpinnerInteractionListener(it1, viewModel) }
                    spnDivision.setOnTouchListener(listener)
                    spnDivision.onItemSelectedListener = listener
                    viewModel.setStateEvent(SubjectStateEvent.GetSubjectEvent(""))
                }
                viewState.selectedGradePosition?.let {
                    Log.d("SAN", "selectedGradePosition-->$it")
                     spnDivision.setSelection(it)
                }
                viewState.subjectList?.let {
                    Log.d("SAN", "subjectList-->" + it.size)
                    binding.rvSubjects.withModels {
                        for (subj in it) {
                            subjectListEpoxyHolder {
                                id(subj.subjectIndex.toLong())
                                title(subj.title)
                                imageUrl(subj.icon)
                                Utility.getDrawable(
                                    subj.icon.substring(
                                        0,
                                        subj.icon.lastIndexOf(".")
                                    ), requireContext()
                                )?.let { it1 ->
                                    imageDrawable(it1)
                                }
                                listener {
                                    val bundle = Bundle()
                                    bundle.putParcelable("subjectdata", subj)
                                    findNavController().navigate(
                                        R.id.action_subjectListFragment_to_topicFragment,
                                        bundle
                                    )
                                }
                            }
                        }

                    }
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



}


fun RequestManager.loadImage(url: String): RequestBuilder<Bitmap> {

    val options = RequestOptions
        .diskCacheStrategyOf(DiskCacheStrategy.NONE)
        .dontAnimate()
        .placeholder(R.drawable.ic_mother)
        .signature(ObjectKey(url.plus("preloading")))

    return asBitmap()
        .apply(options)
        .load(url)
}
