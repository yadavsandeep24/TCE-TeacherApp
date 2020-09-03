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
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.*
import com.tce.teacherapp.databinding.FragmentStudentProfileBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.adapter.childEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.listeners.ChildClickListener
import com.tce.teacherapp.ui.dashboard.students.adapter.IStudentGalleryClickListener
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.parents.fragment_dashboard_home.*
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
) : BaseStudentFragment(R.layout.fragment_student_profile, viewModelFactory), ChildClickListener,
    IStudentGalleryClickListener {

    private lateinit var binding: FragmentStudentProfileBinding
    private var studentID: String = "0"

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)

        binding.iconHome.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconHome.tag = 1
        binding.iconReport.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconReport.tag = 1

        binding.lblDaysAbsent.text = Html.fromHtml("<u>Days Absent</u>")


        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(0,""))
        subscribeObservers()

        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.filterContainer.setOnClickListener {
            val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.visibility = View.VISIBLE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }
        val bottomSheetBehaviorFilterContainer =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(binding.bottomSheetFilterBy)

        bottomSheetBehaviorFilterContainer.state =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorFilterContainer.skipCollapsed = true
        bottomSheetBehaviorFilterContainer.isDraggable = false

        bottomSheetBehaviorFilterContainer.addBottomSheetCallback(object :
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehaviorFilterContainer.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.sortContainer.setOnClickListener {

            if (bottomSheetBehaviorFilterContainer.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.visibility = View.VISIBLE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
            } else {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }
        binding.tvAll.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(1))
        }
        binding.tvFeedback.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(2))
        }

        binding.tvVideo.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(3))
        }

        binding.tvAudio.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(4))
        }

        binding.tvImage.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(5))
        }

        binding.maskLayout.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }
    }
    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentListResponse?.let {
                    var studentVo = it[0]
                    for (tempStudent: StudentListResponseItem in it) {
                        if(tempStudent.id.equals(this.studentID,true)){
                            studentVo = tempStudent
                            break
                        }
                    }
                    if (studentVo != null) {
                        studentID = studentVo.id
                        binding.tvStudentName.text = studentVo.Name
                        binding.tvBloodGroup.text = "Blood Group : " + studentVo.BloodGroup

                        val pattern = "yyyy-MM-dd"
                        val simpleDateFormat = SimpleDateFormat(pattern)
                        val date = simpleDateFormat.parse(studentVo.DOB)
                        val formattedString: String = SimpleDateFormat("dd MMM yyyy").format(date)
                        binding.tvBirthDate.text = formattedString
                    if (!studentVo.Term2ReportStatus && !studentVo.Term1ReportStatus) {
                        binding.cardReportDetails.visibility = View.GONE
                    } else {
                        binding.cardReportDetails.visibility = View.VISIBLE
                        binding.mainReportDetails.setOnClickListener {
                            if (binding.iconReport.tag == 1) {
                                binding.iconReport.tag = 2
                                binding.iconReport.background =
                                    resources.getDrawable(R.drawable.ic_line)
                                binding.reportContainer.visibility = View.VISIBLE
                                binding.reportContainer.removeAllViews()
                                val list = ArrayList<String>()
                                if (studentVo.Term1ReportStatus!!) {
                                    list.add("Term 1 (Jan - May 2020)")
                                }
                                if (studentVo.Term2ReportStatus) {
                                    list.add("Term 2 (Jun - Oct 2020)")
                                }
                                for (strLabel: String in list) {
                                    val view = LayoutInflater.from(context)
                                        .inflate(R.layout.list_item_progress_reports, null, false)
                                    val tvTerm = view.findViewById(R.id.tv_term) as TextView
                                    tvTerm.text = Html.fromHtml("<u>$strLabel</u>")
                                    tvTerm.setOnClickListener {
                                        val bundle = Bundle()
                                        bundle.putParcelable("studentdata",studentVo)
                                        findNavController().navigate(R.id.action_studentProfileFragment_to_progressCardFragment,bundle)
                                    }
                                    binding.reportContainer.addView(view)
                                }

                            } else {
                                binding.iconReport.tag = 1
                                binding.iconReport.background =
                                    resources.getDrawable(R.drawable.ic_add)
                                binding.reportContainer.visibility = View.GONE
                            }
                        }
                    }
                        binding.mainHomeDetails.setOnClickListener {
                            if (binding.iconHome.tag == 1) {
                                binding.iconHome.tag = 2
                                binding.iconHome.background = resources.getDrawable(R.drawable.ic_line)
                                binding.addressContainer.visibility = View.VISIBLE
                                binding.tvAddress.text = studentVo.Address
                                binding.parentDetailsContainer.visibility = View.VISIBLE
                                binding.parentDetailsContainer.removeAllViews()
                                val list = studentVo.ParentList
                                binding.noteContainer.noteMainContainer.visibility = View.VISIBLE
                                binding.tvLblHomeDetails.visibility = View.VISIBLE


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
                                binding.noteContainer.noteMainContainer.visibility = View.GONE
                                binding.tvLblHomeDetails.visibility = View.GONE

                            }
                        }

                        binding.mainSchoolContactDetails.setOnClickListener {
                            if (binding.iconSchoolContact.tag == 1) {
                                binding.iconSchoolContact.tag = 2
                                binding.iconSchoolContact.background = resources.getDrawable(R.drawable.ic_line)
                                binding.schoolAddressContainer.visibility = View.VISIBLE
                                binding.tvSchoolName.text =  studentVo.school
                                binding.tvSchoolAddress.text = studentVo.schooladdress
                                binding.schoolContactDetailsContainer.visibility = View.VISIBLE
                                binding.schoolContactDetailsContainer.removeAllViews()
                                val list = studentVo.TeacherList
                                if (list != null) {
                                    for (teacher: Teacher in list) {
                                        val view = LayoutInflater.from(context)
                                            .inflate(R.layout.list_item_parent_details, null, false)
                                        val tvStudent = view.findViewById(R.id.child_name) as TextView
                                        tvStudent.text = teacher.FirstName + " " + teacher.LastName
                                        val tvContactNo = view.findViewById<TextView>(R.id.tv_contact_no)
                                        tvContactNo.text = teacher.ContactNo
                                        val tvRelation = view.findViewById<TextView>(R.id.tv_relation)
                                        tvRelation.visibility = View.GONE
                                        val tvDivider = view.findViewById<TextView>(R.id.tv_divider)
                                        tvDivider.visibility = View.GONE

                                        binding.schoolContactDetailsContainer.addView(view)
                                    }
                                }
                            } else {
                                binding.iconSchoolContact.tag = 1
                                binding.iconSchoolContact.background = resources.getDrawable(R.drawable.ic_add)
                                binding.schoolAddressContainer.visibility = View.GONE
                                binding.schoolContactDetailsContainer.visibility = View.GONE

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
                }
                    binding.rvFilter.layoutManager = GridLayoutManager(activity, 1)
                    binding.rvFilter.setHasFixedSize(true)
                    val epoxyVisibilityTracker1 = EpoxyVisibilityTracker()
                    epoxyVisibilityTracker1.attach(binding.rvFilter)

                    Utility.setSelectorRoundedCorner(
                        requireContext(),  binding.addChild, 0,
                        R.color.transparent, R.color.dim_color,
                        R.color.transparent, R.color.transparent, 0
                    )

                    binding.addChild.setOnClickListener {
                        findNavController().navigate(R.id.action_studentProfileFragment_to_addChildFragment3)
                    }
                    binding.rvFilter.withModels {
                            for (child in it) {
                                childEpoxyHolder {
                                    id(child.id)
                                    strStudentName(child.Name)
                                    student(child)
                                    childClickListener(this@StudentProfileFragment)
                                }
                            }
                    }
                    viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(1))
                   viewState.studentListResponse = null

                    binding.lblDaysAbsent.setOnClickListener {
                        val bundle = Bundle()
                        val list = studentVo.AbsentData.toList()
                        val arrList = ArrayList<AbsentData>()
                        arrList.addAll(list)
                        bundle.putParcelableArrayList("absentListData",arrList)
                        findNavController().navigate(R.id.action_studentProfileFragment_to_absentDaysCalenderFragment,bundle)
                    }
                }

                viewState.studentportfolioresponse?.let {
                    Log.d("SAN","it.studentPortFolioList.size-->"+it.size)
                    var studentPortFolioDataItem = it[0]
                    for (studentFolio: StudentPortFolioResponseItem in it) {
                        if(studentFolio.id.equals(this.studentID,true)){
                            studentPortFolioDataItem = studentFolio
                            break
                        }
                    }

                   if(studentPortFolioDataItem.Note != null && studentPortFolioDataItem.Note!!.isNotEmpty()) {
                        binding.noteContainer.tvNoteText.text =  studentPortFolioDataItem.Note
                    }
                    if(studentPortFolioDataItem.DaysPresent.isNotEmpty()) {
                        binding.tvPresentCount.text = studentPortFolioDataItem.DaysPresent
                    }
                    if(studentPortFolioDataItem.DaysAbsent.isNotEmpty()) {
                        binding.tvAbsentCount.text = studentPortFolioDataItem.DaysAbsent
                    }

                    binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
                    binding.rvPortfolio.setHasFixedSize(true)
                    val myAdapter = StudentPortfolioAdapter(requireContext(), false,this)
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

    override fun onChildListItemClick(student: StudentListResponseItem) {
        this.studentID = student.id
        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(0,""))
        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
    }

    override fun onItemClick(item: StudentGalleryData) {
        val bundle = Bundle()
        bundle.putParcelable("studentGalleryData", item)

        if (item.contenttype.equals("AV", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_studentProfileFragment_to_studentGalleryVideoPostFragment,
                    bundle
                )
            }
        } else if (item.contenttype.equals("image", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_studentProfileFragment_to_studentGalleryImagePostFragment,
                    bundle
                )
            }
        } else if (item.contenttype.equals("audio", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_studentProfileFragment_to_studentGalleryVideoPostFragment,
                    bundle
                )
            }
        }
    }

    override fun onCheckBoxClicked(item: StudentGalleryData) {
    }

    override fun onDateCheckBoxClicked(item: StudentGalleryResponseItem) {
    }


}