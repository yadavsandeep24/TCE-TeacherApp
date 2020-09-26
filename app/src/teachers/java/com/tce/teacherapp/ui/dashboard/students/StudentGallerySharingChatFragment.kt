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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentStudentGallerySharingChatBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.ISelectedStudentClickListener
import com.tce.teacherapp.ui.dashboard.messages.adapter.NewMessageAdapter
import com.tce.teacherapp.ui.dashboard.messages.adapter.SelectedStudentAdapter
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGallerySharingChatFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_sharing_chat, viewModelFactory),
    ViewHolderClickListener, ISelectedStudentClickListener {

    private lateinit var binding: FragmentStudentGallerySharingChatBinding

    private lateinit var tvSend : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentGallerySharingChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
         (activity as DashboardActivity).setCustomToolbar(R.layout.student_gallery_share_chat_top_bar)
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

            val isClass1Selected =  binding.class1.isChecked
            val isClass2Selected =  binding.class2.isChecked
            if(isClass1Selected && isClass2Selected) {
                viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(0,""))
            }else if(isClass1Selected) {
                viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(1,""))
            }else if(isClass2Selected) {
                viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(5,""))
            }else{
                val adapter = binding.rvNewMessage.adapter  as NewMessageAdapter
                adapter.setData(emptyList())
            }
            false
        }

        val tvBack =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvSelectedStudent.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)

        }
        val selectedStudentAdapter = SelectedStudentAdapter(requireContext(),this)
        binding.rvSelectedStudent.adapter = selectedStudentAdapter

        binding.rvNewMessage.layoutManager = GridLayoutManager(activity, 1)
        binding.rvNewMessage.setHasFixedSize(true)
        val newMessageAdapter = NewMessageAdapter(requireContext(),R.color.pale_grey,this)
        binding.rvNewMessage.adapter = newMessageAdapter
        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(0,""))
        subscribeObservers()

        tvSend= (activity as DashboardActivity).binding.toolBar.findViewById(R.id.tvSend)
        tvSend.visibility = View.GONE
        tvSend.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("flowType",1)
            findNavController().navigate(
                R.id.action_studentGallerSharingChatFragment_to_groupChatFragment2,bundle
            )

        }

        binding.class1.setOnCheckedChangeListener { _, isChecked ->
            val isClass2Checked = binding.class2.isChecked
            if(isChecked) {
                if(isClass2Checked) {
                    viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(0,""))
                }else {
                    viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(1,""))
                }
            }else{
                if(isClass2Checked) {
                    viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(5,""))
                }else{
                    val adapter = binding.rvNewMessage.adapter  as NewMessageAdapter
                    adapter.setData(emptyList())
                }

            }
        }

        binding.class2.setOnCheckedChangeListener { _, isChecked ->
            val isClass1Checked = binding.class1.isChecked
            if(isChecked) {
                if(isClass1Checked) {
                    viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(0,""))
                }else {
                    viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(5,""))
                }
            }else{
                if(isClass1Checked) {
                    viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(1,""))
                }else{
                    val adapter = binding.rvNewMessage.adapter  as NewMessageAdapter
                    adapter.setData(emptyList())
                }

            }
        }
    }
    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            val isClass1Selected =  binding.class1.isChecked
            val isClass2Selected =  binding.class2.isChecked
            if(isClass1Selected && isClass2Selected) {
                viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(0,newText))
            }else if(isClass1Selected) {
                viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(1,newText))
            }else if(isClass2Selected) {
                viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(5,newText))
            }else{
                val adapter = binding.rvNewMessage.adapter  as NewMessageAdapter
                adapter.setData(emptyList())
            }
            return true

        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentListResponse?.let {mainStudentList ->
                    val adapter = binding.rvNewMessage.adapter  as NewMessageAdapter
                    adapter.setData(mainStudentList)
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

    override fun onLongTap(index: Int) {
    }

    override fun onTap(index: Int, item: StudentListResponseItem) {
        val adapter = binding.rvSelectedStudent.adapter  as SelectedStudentAdapter
            binding.rvSelectedStudent.visibility = View.VISIBLE
            tvSend.visibility = View.VISIBLE
            val isExist = adapter.modelList.any{ it.id ==  item.id }
            if(!isExist) {
                adapter.modelList.add(item)
            }
        adapter.notifyDataSetChanged()

    }

    override fun onCheckBoxClicked(item: StudentListResponseItem) {

    }

    override fun onClose(item: StudentListResponseItem) {
        val selectedStudentAdapter = binding.rvSelectedStudent.adapter  as SelectedStudentAdapter
        val newMessageAdapter = binding.rvNewMessage.adapter  as NewMessageAdapter
        selectedStudentAdapter.modelList.remove(item)
        selectedStudentAdapter.notifyDataSetChanged()
        if (selectedStudentAdapter.modelList.isEmpty()) {
            binding.rvSelectedStudent.visibility = View.GONE
            tvSend.visibility = View.GONE
        } else {
            binding.rvSelectedStudent.visibility = View.VISIBLE
            tvSend.visibility = View.VISIBLE
        }
        newMessageAdapter.updateListItem(item)
    }
}