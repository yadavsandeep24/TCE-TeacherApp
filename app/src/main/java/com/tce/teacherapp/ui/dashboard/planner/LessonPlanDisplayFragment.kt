package com.tce.teacherapp.ui.dashboard.planner

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentLessonPlanDisplayBinding
import com.tce.teacherapp.db.entity.LessonPlanPeriod
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.planner.adapter.resourceEpoxyHolder


class LessonPlanDisplayFragment : Fragment() {

    private lateinit var binding : FragmentLessonPlanDisplayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLessonPlanDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lessonPlanPeriod = arguments?.getParcelable("lessonPlanData") as LessonPlanPeriod?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.planner_event_top_bar)
        (activity as DashboardActivity).expandAppBar(true)

        val topBar =
            (activity as DashboardActivity).binding.toolBar.findViewById<RelativeLayout>(R.id.event_top_container)

        val tvBack =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_back) as TextView)
        val toolbarTitle =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.toolbar_title) as TextView)

        tvBack.background = resources.getDrawable(R.drawable.ic_thick_arrow_left_black)

        tvBack.setOnClickListener(View.OnClickListener {
            (activity as DashboardActivity).onBackPressed()
        })
        toolbarTitle.text = "View Lesson Plan"
        toolbarTitle.setTextColor(resources.getColor(R.color.color_black))
        topBar.setBackgroundColor(Color.parseColor("#ffaa1e"))

        binding.tvTopic.text = lessonPlanPeriod!!.Topic
        binding.tvDate.text = lessonPlanPeriod!!.Date
        binding.tvPeriod.text = "Period " + lessonPlanPeriod!!.SequenceNo.toString()
        binding.tvLessonType.text = lessonPlanPeriod!!.LessonTypeValue
        binding.tvTeacherFocus.text = lessonPlanPeriod!!.TeacherFocus
        binding.tvActivity.text = lessonPlanPeriod!!.Activity


        if(lessonPlanPeriod!!.Skills != null && lessonPlanPeriod!!.Skills.size > 0) {
            for (skill in lessonPlanPeriod!!.Skills) {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.list_item_skills, null, false)
                val tvName = view.findViewById(R.id.tv_skill_name) as TextView
                val container = view.findViewById(R.id.container) as LinearLayout
                tvName.text = skill
                container.setPadding(0,0,10,0)
                binding.lnrSkills.addView(view)
            }
        }

        val linearLayoutManager1 = LinearLayoutManager(requireContext())
        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
        binding.rvResource.apply {
            layoutManager = linearLayoutManager1
            setHasFixedSize(true)
        }


        binding.rvResource.withModels {
            lessonPlanPeriod!!.ResourceList?.let {
                for (res in it){
                    resourceEpoxyHolder {
                        id(res.id)
                        name(res.title)
                    }

                }
            }


        }

    }

}