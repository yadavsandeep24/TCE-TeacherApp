package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.api.response.Objective
import com.tce.teacherapp.api.response.ProgressData
import com.tce.teacherapp.databinding.FragmentProgressCardBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.ProgressCardAdapter
import java.util.*


class ProgressCardFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)


        binding.rvProgress.layoutManager = GridLayoutManager(activity, 1)
        binding.rvProgress.setHasFixedSize(true)
        var myAdapter = ProgressCardAdapter(requireContext())
        binding.rvProgress.adapter = myAdapter
        myAdapter?.modelList = getProgressData()
        myAdapter?.notifyDataSetChanged()


    }

    private fun getProgressData(): MutableList<ProgressData> {
        Log.d("TAG", "inside getDummyData")
        val list = ArrayList<ProgressData>()
        val obectiveList = ArrayList<Objective>()
        obectiveList.add(Objective("1", "Identifies letters", 1, ""))
        obectiveList.add(Objective("2", "Tries to associate letter sound with letter", 2, ""))
        obectiveList.add(Objective("3", "Responds to questions when askedr", 2, ""))
        obectiveList.add(Objective("4", "Attempts to speak clearly", 2, ""))
        obectiveList.add(Objective("5", "Attempts to pronounce difficult words", 2, ""))

        list.add(ProgressData("1", "Language Skills", obectiveList, 1, true, 1, 1))
        list.add(ProgressData("2", "Numeracy Skills", obectiveList, 1, true, 1, 1))
        list.add(ProgressData("3", "Cognitive Skills", obectiveList, 1, true, 1, 1))
        list.add(
            ProgressData(
                "4",
                "Personal, Social & Emotional Skills",
                obectiveList,
                1,
                true,
                1,
                1
            )
        )

        Log.d("TAG", "The size is ${list.size}")
        return list
    }

}