package com.tce.teacherapp.ui.dashboard.students

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentProgressCardBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.ProgressCardAdapter
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class ProgressCardFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_progress_card, viewModelFactory){

    private lateinit var binding: FragmentProgressCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProgressCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?
        val bottomSheetBehavior = com.tce.teacherapp.util.sheets.BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object : com.tce.teacherapp.util.sheets.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.imgUpload.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.visibility = View.VISIBLE
            } else {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }
        binding.maskLayout.setOnClickListener {
            bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }
        binding.chatContainer.setOnClickListener {
            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_yesno_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            bottomSheetBehavior.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN

            val patternContainer = dialog.findViewById(R.id.pattern_container) as LinearLayout
            patternContainer.visibility = View.GONE

            val txtTitle = dialog.findViewById(R.id.tv_title) as TextView
            txtTitle.visibility = View.VISIBLE
            val txtMessage = dialog.findViewById(R.id.tv_message) as TextView

            txtMessage.gravity = Gravity.CENTER

            txtTitle.text = "Share to Chat"
            txtMessage.text = "Do you want to share Term 1 Progress Card to Amit Boaz chat group?"

            val dataContainer = dialog.findViewById(R.id.data_container) as LinearLayout
            dataContainer.setPadding(10,0,10,0)

            val txtYes = dialog.findViewById(R.id.tvYes) as TextView
            val txtNo = dialog.findViewById(R.id.tvNo) as TextView

            txtYes.setOnClickListener {
                binding.maskLayout.visibility = View.GONE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                dialog.dismiss()

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

                txtTitle.text = "Report card sent!"

                Handler().postDelayed({
                    dialog.dismiss()
                }, 1000)

            }

            txtNo.setOnClickListener {
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                dialog.dismiss()
            }
            binding.maskLayout.setOnClickListener {
                bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }


        binding.rvProgress.layoutManager = GridLayoutManager(activity, 1)
        binding.rvProgress.setHasFixedSize(true)
        val myAdapter = ProgressCardAdapter(requireContext())
        binding.rvProgress.adapter = myAdapter
        if (studentVo != null) {
            binding.tvStudentName.text = studentVo.Name
            binding.tvSchoolName.text = studentVo.school
            if(!studentVo.studentClass.isNullOrEmpty()) {
                binding.tvClassName.text = studentVo.studentClass
            }
            binding.tvStudentAge.text = "${Utility.getAge(studentVo.DOB)} years old"
            binding.tvTeacherName.text = studentVo.teacher
            if(studentVo.ProgressCard != null) {
                myAdapter.modelList = studentVo.ProgressCard[0].ProgressData
            }
        }
        myAdapter.notifyDataSetChanged()

        binding.tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }

    }

}