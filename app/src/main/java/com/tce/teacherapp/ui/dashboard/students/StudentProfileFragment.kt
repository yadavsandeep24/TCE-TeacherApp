package com.tce.teacherapp.ui.dashboard.students

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Parent
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.api.response.StudentPortFolioResponseItem
import com.tce.teacherapp.databinding.FragmentStudentProfileBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class StudentProfileFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_profile, viewModelFactory) {

    private lateinit var binding: FragmentStudentProfileBinding
    private lateinit var studentID: String

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.student_top_bar)
        (activity as DashboardActivity).expandAppBar(true)


        if (studentVo != null) {
            studentID = studentVo.id
            binding.tvStudentName.text =   studentVo.Name
            binding.tvBloodGroup.text = "Blood Group : "+studentVo.BloodGroup

            val pattern = "yyyy-MM-dd"
            val simpleDateFormat = SimpleDateFormat(pattern)
            val date = simpleDateFormat.parse(studentVo.DOB)
            val formattedString: String =  SimpleDateFormat("dd MMM yyyy").format(date)
            binding.tvBirthDate.text = formattedString
        }
        binding.iconHome.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconHome.tag = 1
        binding.iconReport.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconReport.tag = 1

        binding.mainHomeDetails.setOnClickListener {
            if (binding.iconHome.tag == 1) {
                binding.iconHome.tag = 2
                binding.iconHome.background = resources.getDrawable(R.drawable.ic_line)
                binding.addressContainer.visibility = View.VISIBLE
                binding.tvAddress.text = studentVo?.Address
                binding.parentDetailsContainer.visibility = View.VISIBLE
                binding.parentDetailsContainer.removeAllViews()
                val list = studentVo?.ParentList
                if (list != null) {
                    for (parent: Parent in list) {
                        val view = LayoutInflater.from(context)
                            .inflate(R.layout.list_item_parent_details, null, false)
                        val tvStudent = view.findViewById(R.id.child_name) as TextView
                        tvStudent.text = parent.FirstName + " " + parent.LastName
                        val tvContactNo = view.findViewById<TextView>(R.id.tv_contact_no)
                        tvContactNo.text = parent.ContactNo
                        val tvRelation = view.findViewById<TextView>(R.id.tv_relation)
                        tvRelation.text = parent.Relation
                        binding.parentDetailsContainer.addView(view)
                    }
                }

            } else {
                binding.iconHome.tag = 1
                binding.iconHome.background = resources.getDrawable(R.drawable.ic_add)
                binding.addressContainer.visibility = View.GONE
                binding.parentDetailsContainer.visibility = View.GONE
            }
        }

        if (studentVo != null) {
            if (!studentVo.Term2ReportStatus && !studentVo.Term1ReportStatus) {
                binding.cardReportDetails.visibility = View.GONE
            } else {
                binding.cardReportDetails.visibility = View.VISIBLE
                binding.mainReportDetails.setOnClickListener {
                    if (binding.iconReport.tag == 1) {
                        binding.iconReport.tag = 2
                        binding.iconReport.background = resources.getDrawable(R.drawable.ic_line)
                        binding.reportContainer.visibility = View.VISIBLE
                        binding.reportContainer.removeAllViews()
                        val list = ArrayList<String>()
                        if (studentVo?.Term1ReportStatus!!) {
                            list.add("Term 1 (Jan - May 2020")
                        }
                        if (studentVo.Term2ReportStatus) {
                            list.add("Term 2 (Jun - Oct 2020")
                        }
                        for (strLabel: String in list) {
                            val view = LayoutInflater.from(context)
                                .inflate(R.layout.list_item_progress_reports, null, false)
                            val tvTerm = view.findViewById(R.id.tv_term) as TextView
                            tvTerm.text = Html.fromHtml("<u>$strLabel</u>")
                            tvTerm.setOnClickListener {
                                findNavController().navigate(R.id.action_studentProfileFragment_to_progressCardFragment)
                            }
                            binding.reportContainer.addView(view)
                        }

                    } else {
                        binding.iconReport.tag = 1
                        binding.iconReport.background = resources.getDrawable(R.drawable.ic_add)
                        binding.reportContainer.visibility = View.GONE
                    }
                }
            }
        }

        binding.btnShare.setOnClickListener {
            val bundle = Bundle()
            val list = ((binding.rvPortfolio.adapter as StudentPortfolioAdapter).modelList).toList()
            val arrList = ArrayList<Portfolio>()
            arrList.addAll(list)
            bundle.putParcelableArrayList("portfoliolistdata",arrList)
            bundle.putParcelable("studentdata",studentVo)
            findNavController().navigate(R.id.action_studentProfileFragment_to_shareMediaFragment,bundle)
        }

        binding.btnUpload.setOnClickListener {
            findNavController().navigate(R.id.action_studentProfileFragment_to_studentProfileUploadResouceSelectionFragment)
        }

        val tvTitle =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)
        tvTitle.text = (studentVo?.Name ?: "") + "'s Profile"

        val tvBack =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE
        tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }

        viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio)
        subscribeObservers()
    }
    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentportfolioresponse?.let {
                    Log.d("SAN","it.studentPortFolioList.size-->"+it.size)
                    var studentPortFolioDataItem = it[0]
                    for (studentFolio: StudentPortFolioResponseItem in it) {
                        if(studentFolio.id.equals(studentID,true)){
                            studentPortFolioDataItem = studentFolio
                            break
                        }
                    }

                    if(studentPortFolioDataItem.Note != null && studentPortFolioDataItem.Note!!.isNotEmpty()) {
                        binding.noteContainer.visibility = View.VISIBLE
                        binding.tvNoteText.text =  studentPortFolioDataItem.Note
                    }
                    if(studentPortFolioDataItem.DaysPresent.isNotEmpty()) {
                        binding.tvPresentCount.text = studentPortFolioDataItem.DaysPresent
                    }
                    if(studentPortFolioDataItem.DaysAbsent.isNotEmpty()) {
                        binding.tvAbsentCount.text = studentPortFolioDataItem.DaysAbsent
                    }

                    binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
                    binding.rvPortfolio.setHasFixedSize(true)
                    val myAdapter = StudentPortfolioAdapter(requireContext(), false)
                    binding.rvPortfolio.adapter = myAdapter
                    myAdapter.modelList = studentPortFolioDataItem.Portfolio.toMutableList()
                    myAdapter.notifyDataSetChanged()
                }

            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "SubjectListFragment-->viewModel.stateMessage")

            stateMessage?.let {

                /*uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )*/
            }
        })

    }


}