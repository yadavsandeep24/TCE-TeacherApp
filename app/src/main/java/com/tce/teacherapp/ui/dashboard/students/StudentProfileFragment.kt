package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Feedback
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.SharedListItem
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.databinding.FragmentPortfolioBinding
import com.tce.teacherapp.databinding.FragmentStudentProfileBinding
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.PortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import java.util.ArrayList


class StudentProfileFragment : Fragment() {

    private lateinit var binding : FragmentStudentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        binding.iconHome.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconHome.setTag(1)
        binding.iconReport.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconReport.setTag(1)

        binding.mainHomeDetails.setOnClickListener(View.OnClickListener {
            if(binding.iconHome.tag == 1){
                binding.iconHome.setTag(2)
                binding.iconHome.background = resources.getDrawable(R.drawable.ic_line)
                binding.addressContainer.visibility = View.VISIBLE
                binding.parentDetailsContainer.visibility = View.VISIBLE
                binding.parentDetailsContainer.removeAllViews()
                val list = getDummyData()
                for(student : Student in list){
                    val view = LayoutInflater.from(context).inflate(R.layout.list_item_parent_details, null, false)
                    val tvStudent = view.findViewById(R.id.child_name) as TextView
                    tvStudent.setText(student.name)
                    binding.parentDetailsContainer.addView(view)
                }

            }else{
                binding.iconHome.setTag(1)
                binding.iconHome.background = resources.getDrawable(R.drawable.ic_add)
                binding.addressContainer.visibility = View.GONE
                binding.parentDetailsContainer.visibility = View.GONE
            }
        })

        binding.mainReportDetails.setOnClickListener(View.OnClickListener {
            if(binding.iconReport.tag == 1){
                binding.iconReport.setTag(2)
                binding.iconReport.background = resources.getDrawable(R.drawable.ic_line)
                binding.reportContainer.visibility = View.VISIBLE
                binding.reportContainer.removeAllViews()
                val list = getDummyReport()
                for(student : Student in list){
                    val view = LayoutInflater.from(context).inflate(R.layout.list_item_progress_reports, null, false)
                    val tvStudent = view.findViewById(R.id.tv_term) as TextView
                    tvStudent.setText(student.name)
                    binding.reportContainer.addView(view)
                }

            }else{
                binding.iconReport.setTag(1)
                binding.iconReport.background = resources.getDrawable(R.drawable.ic_add)
                binding.reportContainer.visibility = View.GONE
            }
        })


        binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
        binding.rvPortfolio.setHasFixedSize(true)
        var myAdapter = StudentPortfolioAdapter(requireContext(), false)
        binding.rvPortfolio.adapter = myAdapter
        myAdapter?.modelList = getPortfolioData()
        myAdapter?.notifyDataSetChanged()

        binding.btnShare.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_studentProfileFragment_to_shareMediaFragment)
        })
    }

    private fun getDummyData(): MutableList<Student> {
        Log.d("TAG", "inside getDummyData")
        val list = ArrayList<Student>()
        list.add(Student(1, "Student 1", "", false))
        list.add(Student(2, "Student 2", "", false))
        list.add(Student(3, "Student 3", "", false))


        Log.d("TAG", "The size is ${list.size}")
        return list
    }

    private fun getDummyReport(): MutableList<Student> {
        Log.d("TAG", "inside getDummyData")
        val list = ArrayList<Student>()
        list.add(Student(1, "Term 1 (Jan - May 2020)", "", false))
        list.add(Student(2, "Term 2 (Jan - May 2020)", "", false))


        Log.d("TAG", "The size is ${list.size}")
        return list
    }

    private fun getPortfolioData(): MutableList<Portfolio> {
        Log.d("TAG", "inside getDummyData")
        val list = ArrayList<Portfolio>()
        val feedbackList = ArrayList<Feedback>()
        feedbackList.add(Feedback("1", "Communicate well"))
        feedbackList.add(Feedback("2", "Present daily"))

        val gallaryList = ArrayList<StudentGalleryData>()
        gallaryList.add(StudentGalleryData(ArrayList<SharedListItem>(), "AV", "","SP01","",
            "https://kohantextilejournal.com/wp-content/uploads/2018/04/video-poster.jpg","Title Name 1 - Modality",""))
        gallaryList.add(StudentGalleryData(ArrayList<SharedListItem>(), "AV", "","SP02","",
            "https://googleads.github.io/videojs-ima/examples/posters/bbb_poster.jpg","Title Name 1 - Modality",""))
        gallaryList.add(StudentGalleryData(ArrayList<SharedListItem>(), "AV",
            "","SP03","","https://kohantextilejournal.com/wp-content/uploads/2018/04/video-poster.jpg","Title Name 1 - Modality",""))


        list.add(Portfolio("14 January 2020, Tuesday",   feedbackList, gallaryList, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged"))

        list.add(Portfolio("15 January 2020, Tuesday",   feedbackList, gallaryList, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged"))

        list.add(Portfolio("16 January 2020, Tuesday",   feedbackList, gallaryList, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged"))


        Log.d("TAG", "The size is ${list.size}")
        return list
    }




}