package com.tce.teacherapp.ui.dashboard.messages

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentResourceBinding
import com.tce.teacherapp.db.entity.Resource
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.IResourceItemSelectedListener
import com.tce.teacherapp.ui.dashboard.messages.adapter.IResourceTypeItemSelectedListener
import com.tce.teacherapp.ui.dashboard.messages.adapter.ResourceAdapter
import com.tce.teacherapp.ui.dashboard.messages.adapter.ResourceTypeAdapter
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
) : BaseMessageFragment(R.layout.fragment_resource, viewModelFactory),
    IResourceTypeItemSelectedListener, IResourceItemSelectedListener {

    private lateinit var binding: FragmentResourceBinding

    private var selectedType = "All"
    private var selectedPosition = -1
    private var addedCount = 0

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)


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
            uiCommunicationListener.hideSoftKeyboard()
            binding.svResources.setQuery("", false)
            binding.svResources.clearFocus()
            viewModel.setStateEvent(
                MessageStateEvent.GetResourceSelectionEvent(
                    "",
                    selectedType
                )
            )
            false
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvResourceType.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
        }
        val resourceTypeAdapter = ResourceTypeAdapter(requireContext(), this)
        binding.rvResourceType.adapter = resourceTypeAdapter

        binding.imgBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed(false)
        }

        binding.tvSend.setOnClickListener {
            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_success_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            val txtTitle = dialog.findViewById(R.id.tv_title) as TextView

            txtTitle.text = "$addedCount resource added successfully."

            Handler().postDelayed({
                dialog.dismiss()
                (activity as DashboardActivity).onBackPressed(false)
            }, 1000)


        }

        binding.rvResource.layoutManager = GridLayoutManager(activity, 1)
        binding.rvResource.setHasFixedSize(true)
        val resourceAdapter = ResourceAdapter(requireContext(), this)
        binding.rvResource.adapter = resourceAdapter


        if(!selectedType.equals("All",true)){
            binding.tvAll.background = resources.getDrawable(R.drawable.transparent)
        }else{
            binding.tvAll.background = resources.getDrawable(R.drawable.bg_yellow_white_rounded)
        }
        binding.tvAll.setOnClickListener {
            selectedPosition = -1
            selectedType ="All"
            binding.tvAll.background = resources.getDrawable(R.drawable.bg_yellow_white_rounded)
            val adapter = binding.rvResourceType.adapter as ResourceTypeAdapter
            adapter.setResourceTypeSelectedPosition(selectedPosition)
            viewModel.setStateEvent(
                MessageStateEvent.GetResourceSelectionEvent(
                    "",
                    ""
                )
            )
        }
        viewModel.setStateEvent(MessageStateEvent.GetResourceTypeEvent)
        subscribeObservers()

    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            viewModel.setStateEvent(
                MessageStateEvent.GetResourceSelectionEvent(
                    newText,
                    selectedType
                )
            )
            return true

        }
    }


    @SuppressLint("SetTextI18n")
    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.resourceTypeList?.let {
                    val adapter = binding.rvResourceType.adapter as ResourceTypeAdapter
                    adapter.setData(it, selectedPosition)
                    viewModel.setStateEvent(
                        MessageStateEvent.GetResourceSelectionEvent(
                            "", ""
                        )
                    )
                }
                viewState.selectedResourceList?.let {
                    binding.tvTotalResource.text = "" + it.size + " file found"
                    val adapter = binding.rvResource.adapter as ResourceAdapter
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

    override fun onResourceSelected(resource: Resource) {
        val bundle = Bundle()
        bundle.putString("title", resource.title)
        bundle.putString("url", resource.src)
        when {
            resource.contenttype.equals("av", true) -> {
                bundle.putBoolean("isModality", true)
                findNavController().navigate(
                    R.id.action_resourceFragment_to_videoPlayerFragment3,
                    bundle
                )
            }
            resource.contenttype.equals("Image", true) -> {
                findNavController().navigate(
                    R.id.action_resourceFragment_to_imageContentFragment3,
                    bundle
                )
            }
            resource.contenttype.equals("activity", true) -> {
                findNavController().navigate(
                    R.id.action_resourceFragment_to_HTMLContentFragment3,
                    bundle
                )
            }
            resource.contenttype.equals("audio", true) -> {
            }
        }
    }

    override fun onAddedClicked(item: Resource) {
        if (item.isAdded) {
            addedCount += 1
        } else {
            addedCount -= 1
        }
        if (addedCount < 1) {
            binding.tvSend.visibility = View.GONE
            binding.tvCount.visibility = View.GONE
        } else {
            binding.tvCount.text = addedCount.toString()
            binding.tvSend.visibility = View.VISIBLE
            binding.tvCount.visibility = View.VISIBLE
        }

    }

    override fun onResourceTypeSelected(item: Resource, position: Int) {
        selectedType = item.ResourceOriginator.toString()
        selectedPosition = position
        binding.tvAll.background = resources.getDrawable(R.drawable.transparent)
        val adapter = binding.rvResourceType.adapter as ResourceTypeAdapter
        adapter.notifyDataSetChanged()
        viewModel.setStateEvent(
            MessageStateEvent.GetResourceSelectionEvent(
                "",
                item.ResourceOriginator!!
            )
        )
    }


}
