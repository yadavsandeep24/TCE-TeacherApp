package com.tce.teacherapp.ui.dashboard.students

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.CalendarDayMarkCompletedBinding
import com.tce.teacherapp.databinding.FragmentMessageListBinding
import com.tce.teacherapp.databinding.FragmentStudentListBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.messages.BaseMessageFragment
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.planner.daysOfWeekFromLocale
import com.tce.teacherapp.ui.dashboard.planner.setTextColorRes
import com.tce.teacherapp.ui.dashboard.students.state.STUDENT_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import com.tce.teacherapp.util.calenderView.utils.yearMonth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.teachers.fragment_dashboard_home.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.jetbrains.anko.childrenSequence
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
) : BaseStudentFragment(R.layout.fragment_student_list,viewModelFactory) {

    private lateinit var binding: FragmentStudentListBinding

    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "StudentViewState: inState is NOT null")
            (inState[STUDENT_VIEW_STATE_BUNDLE_KEY] as StudentViewState?)?.let { viewState ->
                Log.d(TAG, "StudentViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(true)

        viewModel.setStateEvent(StudentStateEvent.GetStudentEvent(""))

        val bottomSheetBehavior =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
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

        binding.classContainer.setOnClickListener {
            viewModel.setStateEvent(DashboardStateEvent.GetUserClassList)
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

        /* binding.maskLayout.setOnClickListener{
             if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED) {
                 bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                 binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                 (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
             }
         }*/



        calenderDialog()


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

}
