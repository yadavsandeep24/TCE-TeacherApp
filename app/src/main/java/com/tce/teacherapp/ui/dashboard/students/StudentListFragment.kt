package com.tce.teacherapp.ui.dashboard.students

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.edgedevstudio.example.recyclerviewmultiselect.MainInterface
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.CalendarDayMarkCompletedBinding
import com.tce.teacherapp.databinding.FragmentStudentListBinding
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.planner.daysOfWeekFromLocale
import com.tce.teacherapp.ui.dashboard.planner.setTextColorRes
import com.tce.teacherapp.ui.dashboard.students.adapter.AttendanceAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.util.calenderView.utils.yearMonth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.teachers.fragment_dashboard_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.jetbrains.anko.childrenSequence
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class StudentListFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_list,viewModelFactory), MainInterface {

     companion object {
        var isMultiSelectOn = false
        val TAG = "MainActivity"
    }
    private lateinit var binding: FragmentStudentListBinding

    private lateinit var classContainer : LinearLayout
    private lateinit var editAttendanceContainer : RelativeLayout
    private lateinit var tvSave : TextView

    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    var myAdapter: AttendanceAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)
        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object : com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        (activity as DashboardActivity).setCustomToolbar(R.layout.student_header_layout)

        classContainer =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.class_container) as LinearLayout)
        editAttendanceContainer =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.edit_attendance_container) as RelativeLayout)
        tvSave =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tvSave) as TextView)
        val tvClassTitle =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_class_title) as TextView)

        tvClassTitle.setText("Attendance")

        classContainer.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }

        binding.tvPortfolio.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_studentListFragment_to_portfolioFragment)
        })

        /* binding.maskLayout.setOnClickListener{
             if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED) {
                 bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                 binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                 (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
             }
         }*/


        if (myAdapter?.selectedIds != null && myAdapter?.selectedIds!!.size > 0) {
            classContainer.visibility = View.GONE
            editAttendanceContainer.visibility = View.VISIBLE
        }else{
            classContainer.visibility = View.VISIBLE
            editAttendanceContainer.visibility = View.GONE
        }

        tvSave.setOnClickListener {
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
            txtTitle.text = "Attendance Updated!"

            Handler().postDelayed({
                myAdapter?.deleteSelectedIds()
                myAdapter?.notifyDataSetChanged()
                classContainer.visibility = View.VISIBLE
                editAttendanceContainer.visibility = View.GONE
                dialog.dismiss()
            }, 1000)

        }
        binding.dateContainer.setOnClickListener {
            calenderDialog()
        }
        val formattedString: String =  SimpleDateFormat("dd MMMM yyyy EEEE").format(Date())
        binding.tvDate.text = formattedString

        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent)
        //viewModel.setStateEvent(StudentStateEvent.GetAttendanceData)
      //  viewModel.setStateEvent(StudentStateEvent.GetFeedbackMaster)
       // viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio)
       //viewModel.setStateEvent(StudentStateEvent.GetGalleryData)

        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentListResponse?.let {
                    Log.d("SAN","it.studentList.size-->"+it.size)
                    isMultiSelectOn = false
                    binding.rvAttendance.layoutManager = GridLayoutManager(activity, 3)
                    binding.rvAttendance.setHasFixedSize(true)
                    myAdapter = AttendanceAdapter(requireContext(), this@StudentListFragment)
                    binding.rvAttendance.adapter = myAdapter
                    myAdapter?.modelList = getDummyData()
                    myAdapter?.notifyDataSetChanged()
                }
                viewState.studentattendancedata?.let {
                    Log.d("SAN","it.studentAttendanceList.size-->"+it.size)
                }
                viewState.feedbackMaster?.let {
                    Log.d("SAN","it.feedbackList.size-->"+it.size)
                }
                viewState.studentportfolioresponse?.let {
                    Log.d("SAN","it.studentPortFolioList.size-->"+it.size)
                }
                viewState.studentgallerydata?.let {
                    Log.d("SAN","it.galleryList.size-->"+it.size)
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

    private fun calenderDialog(){
        val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.calender_mark_completed)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(resources.getColor(android.R.color.transparent))
        )
        dialog.show()

        val legendLayout = dialog.findViewById(R.id.legendLayout) as LinearLayout
        val exOneCalendar = dialog.findViewById(R.id.exOneCalendar) as CalendarView
        val exOneYearText = dialog.findViewById(R.id.exOneYearText) as TextView
        val exOneMonthText = dialog.findViewById(R.id.exOneMonthText) as TextView

        val imgPrevious = dialog.findViewById(R.id.img_previous) as AppCompatImageView
        val imgNext = dialog.findViewById(R.id.img_next) as AppCompatImageView

        legendLayout.month_view.visibility = View.GONE

        val daysOfWeek = daysOfWeekFromLocale()
        legendLayout.day_view.childrenSequence().forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase(
                    Locale.ENGLISH)
                setTextColorRes(R.color.calender_text_grey)
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10)
        val endMonth = currentMonth.plusMonths(10)
        exOneCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        exOneCalendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val textView = CalendarDayMarkCompletedBinding.bind(view).exOneDayText

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDates.contains(day.date)) {
                            selectedDates.remove(day.date)
                        } else {
                            selectedDates.add(day.date)
                        }
                        exOneCalendar.notifyDayChanged(day)
                    }
                    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, EEEE")
                    val formattedString: String = day.date.format(formatter)
                    binding.tvDate.text = formattedString

                    dialog.dismiss()
                }
            }
        }

        exOneCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    when {
                        selectedDates.contains(day.date) -> {
                            textView.setTextColorRes(R.color.color_black)
                            textView.setBackgroundResource(R.drawable.ic_date_selected)
                        }
                        today == day.date -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.ic_today_date_selected)
                        }
                        else -> {
                            textView.setTextColorRes(R.color.color_black)
                            textView.background = null
                        }
                    }
                } else {
                    textView.setTextColorRes(R.color.calender_text_grey_light)
                    textView.background = null
                }
            }
        }

        exOneCalendar.monthScrollListener = {
            if (exOneCalendar.maxRowCount == 6) {
                exOneYearText.text = it.yearMonth.year.toString()
                exOneMonthText.text = monthTitleFormatter.format(it.yearMonth)
            } else {
                // In week mode, we show the header a bit differently.
                // We show indices with dates from different months since
                // dates overflow and cells in one index can belong to different
                // months/years.
                val firstDate = it.weekDays.first().first().date
                val lastDate = it.weekDays.last().last().date
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    exOneYearText.text = firstDate.yearMonth.year.toString()
                    exOneMonthText.text = monthTitleFormatter.format(firstDate)
                } else {
                    exOneMonthText.text =
                        "${monthTitleFormatter.format(firstDate)} - ${monthTitleFormatter.format(lastDate)}"
                    if (firstDate.year == lastDate.year) {
                        exOneYearText.text = firstDate.yearMonth.year.toString()
                    } else {
                        exOneYearText.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    }
                }
            }
        }



    }


    override fun mainInterface(size: Int) {
        if (size > 0) {
            classContainer.visibility = View.GONE
            editAttendanceContainer.visibility = View.VISIBLE
        }else{
            classContainer.visibility = View.VISIBLE
            editAttendanceContainer.visibility = View.GONE
        }

    }
    private fun getDummyData(): MutableList<Student> {
        Log.d(TAG, "inside getDummyData")
        val list = ArrayList<Student>()
        list.add(Student(1, "Student 1", "", false))
        list.add(Student(2, "Student 2", "", false))
        list.add(Student(3, "Student 3", "", false))
        list.add(Student(4, "Student 4", "", false))
        list.add(Student(5, "Student 5", "", false))
        list.add(Student(6, "Student 6", "", false))
        list.add(Student(7, "Student 7", "", false))
        list.add(Student(8, "Student 8", "", false))
        list.add(Student(9, "Student 9", "", false))
        list.add(Student(10, "Student 10", "", false))
        list.add(Student(11, "Student 11", "", false))
        list.add(Student(12, "Student 12","", false))
        list.add(Student(13, "Student 13", "", false))


        Log.d(TAG, "The size is ${list.size}")
        return list
    }

}
