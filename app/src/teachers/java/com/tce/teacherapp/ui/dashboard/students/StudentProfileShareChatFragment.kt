package com.tce.teacherapp.ui.dashboard.students

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentStudentProfileShareChatBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentProfileShareChatAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentProfileShareChatFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_profile_share_chat, viewModelFactory) {

    private lateinit var binding : FragmentStudentProfileShareChatBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentProfileShareChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val portFolioList:ArrayList<Portfolio>? = arguments?.getParcelableArrayList("portfoliolistdata")
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.student_profile_share_chat_top_bar)
        (activity as DashboardActivity).expandAppBar(true)
        val tvTitle = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)
        tvTitle.text = "Send to "+ (studentVo?.Name ?: "")
        val tvSubTitle = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_subtitle)
        tvSubTitle.text = portFolioList?.size.toString()+" Files Selected"

        val tvBack =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE
        tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }
        binding.rvShareChat.layoutManager = GridLayoutManager(activity, 1)
        binding.rvShareChat.setHasFixedSize(true)
        val myAdapter = StudentProfileShareChatAdapter(requireContext())
        binding.rvShareChat.adapter = myAdapter
        if (portFolioList != null) {
            myAdapter.modelList =portFolioList.toMutableList()
        }
        myAdapter.notifyDataSetChanged()

        binding.imgSendMessage.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
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
            txtTitle.text = portFolioList?.size.toString()+" Files Shared Successfully"

            Handler().postDelayed({
                dialog.dismiss()
                val bundle = Bundle()
                bundle.putParcelable("studentdata",studentVo)
               findNavController().navigate(R.id.action_studentProfileShareChatFragment_to_studentProfileFragment,bundle)
            }, 1000)

        }
    }

}