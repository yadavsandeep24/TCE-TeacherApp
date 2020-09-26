package com.tce.teacherapp.ui.dashboard.messages

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentMessageListBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.messageListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.loadImage
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.message_bottom_filter.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class MessageListFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseMessageFragment(R.layout.fragment_message_list, viewModelFactory) {

    private lateinit var binding: FragmentMessageListBinding

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageListBinding.inflate(inflater, container, false)
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
        if (resources.getString(R.string.app_type)
                .equals(resources.getString(R.string.app_type_parent))
        ) {
            binding.ivUser.visibility = View.VISIBLE
            binding.imgNewMessageLeft.visibility = View.VISIBLE
            binding.imgNewMessage.visibility = View.GONE

        } else {
            binding.ivUser.visibility = View.GONE
            binding.imgNewMessageLeft.visibility = View.GONE
            binding.imgNewMessage.visibility = View.VISIBLE
        }
        val searchText: TextView = binding.svMessage.findViewById(R.id.search_src_text) as TextView
        val myCustomFont: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Rubik-Medium.ttf")
        searchText.typeface = myCustomFont
        searchText.textSize = requireActivity().resources.getDimension(R.dimen.font_size_14_px)

        val searchIcon = binding.svMessage.findViewById(R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.search)

        binding.svMessage.setOnQueryTextListener(queryTextListener)
        // Get the search close button image view
        val closeButton: ImageView =
            binding.svMessage.findViewById(R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
            val t: Toast = Toast.makeText(activity, "close", Toast.LENGTH_SHORT)
            t.show()
            uiCommunicationListener.hideSoftKeyboard()
            binding.svMessage.setQuery("", false)
            binding.svMessage.clearFocus()
            viewModel.setStateEvent(MessageStateEvent.GetMessageEvent(""))
            false
        }

        viewModel.setStateEvent(MessageStateEvent.GetMessageEvent(""))

        val bottomSheetBehavior =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet_layout)

        bottomSheetBehavior.state =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.skipCollapsed = true

        bottomSheetBehavior.addBottomSheetCallback(object :
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })
        binding.imgFilter.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                binding.maskLayout.visibility = View.VISIBLE
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
            } else {
                binding.maskLayout.visibility = View.GONE
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }
        binding.maskLayout.setOnClickListener {
            bottomSheetBehavior.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }
        binding.imgNewMessage.setOnClickListener {
            /* val bundle = Bundle()
             bundle.putParcelable("messageData", )*/
            uiCommunicationListener.hideSoftKeyboard()
            findNavController().navigate(
                R.id.action_messageListFragment_to_newMessageFragment
                //bundle
            )
        }

        binding.imgNewMessageLeft.setOnClickListener {
            /* val bundle = Bundle()
             bundle.putParcelable("messageData", )*/
            uiCommunicationListener.hideSoftKeyboard()
            findNavController().navigate(
                R.id.action_messageListFragment_to_newMessageFragment
                //bundle
            )
        }

        binding.rvMessage.layoutManager = GridLayoutManager(activity, 1)
        binding.rvMessage.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvMessage)
        binding.rvMessage.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, model: SubjectListEpoxyHolder, _ ->
                requestManager.loadImage(model.imageUrl)
            }
        )
        uiCommunicationListener.hideSoftKeyboard()
        subscribeObservers()
    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            viewModel.setStateEvent(MessageStateEvent.GetMessageEvent(newText))
            return true

        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.messageResponse?.let {
                    Log.d("SAN", "messageList-->" + (it.messageList?.size ?: 0))
                    uiCommunicationListener.hideSoftKeyboard()
                    binding.tvSubTitle.text = "(" + it.totalUnReadCount + " Unread)"
                    binding.rvMessage.withModels {
                        var postion = 0
                        for (msg in it.messageList!!) {
                            postion += 1
                            messageListEpoxyHolder {
                                id(postion)
                                strMessage(msg.Title)
                                strDetail(if (msg.Message.isNullOrEmpty()) "" else msg.Message)
                                strCount(msg.unReadCount.toString())
                                strTime(msg.SendDate)
                                messageType(msg.Type)
                                listener {
                                    val bundle = Bundle()
                                    bundle.putParcelable("messageData", msg)
                                    findNavController().navigate(
                                        R.id.action_messageListFragment_to_messageDetailFragment,
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
