package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.api.response.Feedback
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.SharedListItem
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.databinding.FragmentShareMediaBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import java.util.*


class ShareMediaFragment : Fragment() {

    private lateinit var binding : FragmentShareMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)

        binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
        binding.rvPortfolio.setHasFixedSize(true)
        val myAdapter = StudentPortfolioAdapter(requireContext(), true)
        binding.rvPortfolio.adapter = myAdapter
        myAdapter.modelList = getPortfolioData()
        myAdapter.notifyDataSetChanged()

    }

    private fun getPortfolioData(): MutableList<Portfolio> {
        Log.d("TAG", "inside getDummyData")
        val list = ArrayList<Portfolio>()
        val feedbackList = ArrayList<Feedback>()
        feedbackList.add(Feedback("1", "Communicate well"))
        feedbackList.add(Feedback("2", "Present daily"))

        val gallaryList = ArrayList<StudentGalleryData>()
        gallaryList.add(
            StudentGalleryData(
                ArrayList<SharedListItem>(), "AV", "","SP01","",
            "https://kohantextilejournal.com/wp-content/uploads/2018/04/video-poster.jpg","Title Name 1 - Modality","")
        )
        gallaryList.add(
            StudentGalleryData(
                ArrayList<SharedListItem>(), "AV", "","SP02","",
            "https://googleads.github.io/videojs-ima/examples/posters/bbb_poster.jpg","Title Name 1 - Modality","")
        )
        gallaryList.add(
            StudentGalleryData(
                ArrayList<SharedListItem>(), "AV",
            "","SP03","","https://kohantextilejournal.com/wp-content/uploads/2018/04/video-poster.jpg","Title Name 1 - Modality","")
        )


        list.add(Portfolio("14 January 2020, Tuesday",   feedbackList, gallaryList, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged"))

        list.add(Portfolio("15 January 2020, Tuesday",   feedbackList, gallaryList, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged"))

        list.add(Portfolio("16 January 2020, Tuesday",   feedbackList, gallaryList, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged"))


        Log.d("TAG", "The size is ${list.size}")
        return list
    }



}