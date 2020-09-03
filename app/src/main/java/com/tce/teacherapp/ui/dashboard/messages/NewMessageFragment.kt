package com.tce.teacherapp.ui.dashboard.messages

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentNewMessageBinding
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.newMessageEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.adapter.selectedStudentEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class NewMessageFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseMessageFragment(R.layout.fragment_new_message, viewModelFactory) {

    private lateinit var binding: FragmentNewMessageBinding
    private var studentList: ArrayList<StudentListResponseItem>? = ArrayList()
    private var mainList: ArrayList<Student>? = ArrayList()

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
        binding = FragmentNewMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.

        viewState?.studentList = ArrayList()

        outState.putParcelable(
            MESSAGE_VIEW_STATE_BUNDLE_KEY,
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
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)


        val searchText: TextView =
            binding.svNewMessage.findViewById(R.id.search_src_text) as TextView
        val myCustomFont: Typeface =
            Typeface.createFromAsset(requireActivity().assets, "fonts/Rubik-Medium.ttf")
        searchText.typeface = myCustomFont
        searchText.textSize = requireActivity().resources.getDimension(R.dimen.font_size_14_px)

        val searchIcon = binding.svNewMessage.findViewById(R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.search)

        binding.svNewMessage.setOnQueryTextListener(queryTextListener)
        // Get the search close button image view
        val closeButton: ImageView =
            binding.svNewMessage.findViewById(R.id.search_close_btn) as ImageView

        closeButton.setOnClickListener {
            val t: Toast = Toast.makeText(activity, "close", Toast.LENGTH_SHORT)
            t.show()
            uiCommunicationListener.hideSoftKeyboard()
            binding.svNewMessage.setQuery("", false)
            binding.svNewMessage.clearFocus()
            viewModel.setStateEvent(MessageStateEvent.GetStudentEvent(0,""))
            false
        }

        viewModel.setStateEvent(MessageStateEvent.GetStudentEvent(0,""))

        binding.imgBack.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvSelectedStudent.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)

        }

        binding.rvNewMessage.layoutManager = GridLayoutManager(activity, 1)
        binding.rvNewMessage.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvNewMessage)
        subscribeObservers()

        binding.tvNext.setOnClickListener {

            val bundle = Bundle()
            bundle.putParcelableArrayList("studentList", studentList!!)
            findNavController().navigate(
                R.id.action_newMessageFragment_to_groupChatFragment,
                bundle
            )

        }

    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
              viewModel.setStateEvent(MessageStateEvent.GetStudentEvent(0,newText))
            return true

        }
    }


    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentList?.let {
                    Log.d("SAN", "messageList-->" + it.size)
                    binding.rvNewMessage.withModels {
                        for (msg in it) {
                            newMessageEpoxyHolder {
                                id(msg.id.toLong())
                                studentVo(msg)
                                listener {
                                    if (studentList!!.size == 0) {
                                        binding.rvSelectedStudent.visibility =
                                            View.GONE
                                        binding.tvNext.visibility = View.GONE
                                    } else {
                                        binding.rvSelectedStudent.visibility =
                                            View.VISIBLE
                                        binding.tvNext.visibility = View.VISIBLE
                                    }
                                    val isExist = studentList!!.any{ it.id ==  msg.id }
                                    if(!isExist) {
                                        studentList!!.add(msg)
                                        binding.rvSelectedStudent.visibility = View.VISIBLE
                                        binding.rvSelectedStudent.withModels {
                                            for (student in studentList!!) {
                                                selectedStudentEpoxyHolder {
                                                    id(student.id.toLong())
                                                    strName(student.Name)
                                                    listener {
                                                        studentList!!.remove(student)
                                                        requestModelBuild()

                                                        if (studentList!!.size == 0) {
                                                            studentList!!.clear()
                                                            binding.rvSelectedStudent.visibility =
                                                                View.GONE
                                                        } else {
                                                            binding.rvSelectedStudent.visibility =
                                                                View.VISIBLE
                                                        }
                                                    }
                                                }
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
