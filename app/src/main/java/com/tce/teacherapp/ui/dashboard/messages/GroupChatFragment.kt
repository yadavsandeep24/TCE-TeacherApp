package com.tce.teacherapp.ui.dashboard.messages

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentGroupChatBinding
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_group_chat.*


class GroupChatFragment : Fragment() {

   private lateinit var binding : FragmentGroupChatBinding
    private lateinit var studentList : ArrayList<Student>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentList = arguments?.getParcelableArrayList<Student>("studentList") as  ArrayList<Student>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)

        var studentNames = ""
        for (student in studentList){
          studentNames = studentNames + ", " + student.name
        }

        if(!TextUtils.isEmpty(studentNames)){
          studentNames = studentNames.replaceFirst(",","")
        }

        binding.tvTitle1.setText(studentNames)
        binding.tvSubTitle1.setText(studentList.size.toString() + " Members")

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.skipCollapsed = true

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val rotation = when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> 0f
                    BottomSheetBehavior.STATE_COLLAPSED -> 180f
                    BottomSheetBehavior.STATE_HIDDEN -> 180f
                    else -> return
                }

            }
        })

        binding.imgAttachment.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })

        binding.mainContainer.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

        })

    }
}
