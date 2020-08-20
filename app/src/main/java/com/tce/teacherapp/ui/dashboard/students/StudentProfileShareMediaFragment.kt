package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentShareMediaBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class StudentProfileShareMediaFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_share_media, viewModelFactory) {

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
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
        val portFolioList:ArrayList<Portfolio>? = arguments?.getParcelableArrayList("portfoliolistdata")
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?
        binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
        binding.rvPortfolio.setHasFixedSize(true)
        val myAdapter = StudentPortfolioAdapter(requireContext(), true)
        binding.rvPortfolio.adapter = myAdapter
        if (portFolioList != null) {
            myAdapter.modelList =portFolioList.toMutableList()
        }
        myAdapter.notifyDataSetChanged()

        binding.tvBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.tvSend.setOnClickListener {
            val bundle = Bundle()
            val list = ((binding.rvPortfolio.adapter as StudentPortfolioAdapter).modelList).toList()
            val arrList = ArrayList<Portfolio>()
            arrList.addAll(list)
            bundle.putParcelableArrayList("portfoliolistdata",arrList)
            bundle.putParcelable("studentdata",studentVo)
            findNavController().navigate(R.id.action_shareMediaFragment_to_studentProfileShareChatFragment,bundle)
        }

    }
}