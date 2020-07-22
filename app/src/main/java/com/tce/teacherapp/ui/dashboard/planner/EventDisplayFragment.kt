package com.tce.teacherapp.ui.dashboard.planner

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentEventDisplayBinding
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.dashboard.DashboardActivity


class EventDisplayFragment : Fragment() {

    private lateinit var binding : FragmentEventDisplayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventVo = arguments?.getParcelable("eventData") as Event?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.planner_event_top_bar)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        val topBar =
            (activity as DashboardActivity).binding.toolBar.findViewById<RelativeLayout>(R.id.event_top_container)

        val tvBack =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_back) as TextView)
        val toolbarTitle =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.toolbar_title) as TextView)

        tvBack.setOnClickListener(View.OnClickListener {
            (activity as DashboardActivity).onBackPressed()
        })

        toolbarTitle.setText(eventVo!!.type)
        topBar.setBackgroundColor(Color.parseColor(eventVo!!.iconBackColor))

        if(eventVo!!.type.equals("Event", ignoreCase = true)){
            binding.eventContainer.visibility = View.VISIBLE
            binding.todoContainer.visibility = View.GONE
            binding.tvTitle.setText(eventVo!!.eventName)
            binding.tvDate.setText(eventVo!!.date)
            binding.tvNote.setText(eventVo!!.note)
            binding.tvPreview.text = Html.fromHtml("<u>Preview Here</u>")
            eventVo!!.eventImageUrl
            binding.tvPreview.setOnClickListener(View.OnClickListener {
                val bundle = Bundle()
                bundle.putString("url",eventVo!!.eventImageUrl)
                findNavController().navigate(R.id.action_eventDisplayFragment_to_imageContentFragment2,bundle)
            })
            binding.tvImgEvent.setText("SportsDay.jpg")
        }else{
            binding.eventContainer.visibility = View.GONE
            binding.todoContainer.visibility = View.VISIBLE
            binding.tvTodoTitle.setText(eventVo!!.type)
            binding.tvTodoTitleValue.setText(eventVo!!.eventName)
            binding.tvTodoDate.setText(eventVo!!.date)
        }


    }

}