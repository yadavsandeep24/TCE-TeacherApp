package com.tce.teacherapp.ui.dashboard.students

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentStudentGalleryTagStudentBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.newMessageEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.students.adapter.selectedStudentTagEpoxyHolder
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryTagStudentFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_tag_student, viewModelFactory) {

    private lateinit var binding: FragmentStudentGalleryTagStudentBinding
    private var studentList: ArrayList<StudentListResponseItem>? = ArrayList()
    private var mainStudentList: List<StudentListResponseItem>? = listOf()
    private lateinit var tvSend: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentGalleryTagStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.student_top_bar)
        (activity as DashboardActivity).expandAppBar(true)


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
            viewModel.setStateEvent(MessageStateEvent.GetStudentEvent(""))
            false
        }

        val tvBack =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.setOnClickListener(View.OnClickListener {
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
        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent)
        subscribeObservers()

        tvSend = (activity as DashboardActivity).binding.toolBar.findViewById(R.id.tvDone)
        tvSend.setOnClickListener {
            activity?.onBackPressed()

        }
    }

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            //viewModel.setStateEvent(MessageStateEvent.GetStudentEvent(newText))
            return true

        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentListResponse?.let { mainSList ->
                    mainStudentList = mainSList
                    binding.rvNewMessage.withModels {
                        for (msg in mainStudentList!!) {
                            msg.selectedColor = R.color.ice
                            newMessageEpoxyHolder {
                                id(msg.id.toLong())
                                studentVo(msg)
                                listener {
                                    if (studentList!!.isEmpty()) {
                                        binding.rvSelectedStudent.visibility =
                                            View.GONE
                                        tvSend.visibility = View.GONE
                                    } else {
                                        binding.rvSelectedStudent.visibility =
                                            View.VISIBLE
                                        tvSend.visibility = View.VISIBLE
                                    }
                                    val isExist = studentList!!.any { it.id == msg.id }
                                    if (!isExist) {
                                        studentList?.add(msg)
                                        binding.rvSelectedStudent.visibility = View.VISIBLE
                                        binding.rvSelectedStudent.withModels {
                                            for (student in studentList!!) {
                                                selectedStudentTagEpoxyHolder {
                                                    id(student.id.toLong())
                                                    strName(student.Name)
                                                    listener {
                                                        studentList!!.remove(student)
                                                        requestModelBuild()

                                                        if (studentList!!.isEmpty()) {
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