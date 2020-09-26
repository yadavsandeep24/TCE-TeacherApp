package com.tce.teacherapp.ui.dashboard.students

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.AbsentData
import com.tce.teacherapp.databinding.CalendarAbsentDayBinding
import com.tce.teacherapp.databinding.CalendarHeaderBinding
import com.tce.teacherapp.databinding.FragmentAbsentDaysCalenderBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.planner.daysOfWeekFromLocale
import com.tce.teacherapp.ui.dashboard.planner.setTextColorRes
import com.tce.teacherapp.util.calenderView.model.CalendarDay
import com.tce.teacherapp.util.calenderView.model.CalendarMonth
import com.tce.teacherapp.util.calenderView.model.DayOwner
import com.tce.teacherapp.util.calenderView.ui.DayBinder
import com.tce.teacherapp.util.calenderView.ui.MonthHeaderFooterBinder
import com.tce.teacherapp.util.calenderView.ui.ViewContainer
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AbsentDaysCalenderFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_absent_days_calender, viewModelFactory) {

    private lateinit var binding: FragmentAbsentDaysCalenderBinding
    private val today = LocalDate.now()
    var absentdata:ArrayList<AbsentData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAbsentDaysCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         absentdata  = arguments?.getParcelableArrayList("absentListData")!!
        (activity as DashboardActivity).setCustomToolbar(R.layout.student_top_bar)
        (activity as DashboardActivity).expandAppBar(true)

        val tvClassTitle =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.toolbar_title) as TextView)
        tvClassTitle.text = "Absent days"

        val tvBack =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }

        val daysOfWeek = daysOfWeekFromLocale()

        val currentYearMonth = YearMonth.now()
        Log.d("SAN", "currentYearMonth-->$currentYearMonth")
        binding.exFiveCalendar.setup(currentYearMonth.minusMonths(11), currentYearMonth.plusMonths(11), daysOfWeek.first())
        binding.exFiveCalendar.scrollToMonth(currentYearMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarAbsentDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    Log.d("SAN", "Calender_Click-->")
                }
            }
        }


        binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day

                val textView = container.binding.exFiveDayText
                val layout = container.binding.exFiveDayLayout
                val vwClose = container.binding.vwClose
                val tvEvent = container.binding.tvEvent
                textView.text = day.date.dayOfMonth.toString()
                val strDayOfWeek = day.date.dayOfWeek
                if (strDayOfWeek == DayOfWeek.SUNDAY || strDayOfWeek == DayOfWeek.SATURDAY) {
                    layout.background =
                        resources.getDrawable(R.drawable.calender_day_weekend_border)
                } else {
                    layout.background = resources.getDrawable(R.drawable.calender_day_border)
                }
                if (day.owner == DayOwner.THIS_MONTH) {
                    if (day.date == today) {
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_oval_blue))
                    } else {
                        textView.setTextColorRes(R.color.calender_text_grey)
                        textView.background = null
                    }

                } else {
                    textView.setTextColorRes(R.color.calender_text_grey_light)
                    textView.background = null
                }

                val dayAbsentist = absentdata!!.filter {
                    it.Date.contains(
                        day.date.toString(),
                        ignoreCase = true
                    )
                }
                if (dayAbsentist.isNotEmpty()) {
                    val isHoliday= dayAbsentist[0].IsHoliday
                    val msg = dayAbsentist[0].Msg
                    val isAbsent = dayAbsentist[0].IsAbsent

                     if(isAbsent) {
                         vwClose.visibility = View.VISIBLE
                         layout.background =  resources.getDrawable(R.drawable.calender_day_holiday_border)
                         if(msg.isNotEmpty()) {
                             tvEvent.visibility = View.VISIBLE
                             tvEvent.text =  msg
                         }
                     }

                    if(isHoliday){
                        if(msg.isNotEmpty()) {
                            tvEvent.visibility = View.VISIBLE
                            tvEvent.text =  msg
                        }
                    }
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        binding.exFiveCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)

            @SuppressLint("NewApi", "SetTextI18n")
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
  /*              if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    *//*        container.legendLayout.day_view.childrenSequence().map { it as TextView }.forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                    .toUpperCase(Locale.ENGLISH)
                                tv.setTextColorRes(R.color.calender_text_grey)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            }*//*
                    month.yearMonth
                    container.legendLayout.tv_month.text =
                        "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"

                } else {
                    val yearMont = container.legendLayout.tag as YearMonth
                    container.legendLayout.tv_month.text =
                        "${yearMont.month.name.toLowerCase().capitalize()} ${yearMont.year}"
                }*/
                val yearMont = month.yearMonth
                container.legendLayout.tv_month.text =
                    "${yearMont.month.name.toLowerCase().capitalize()} ${yearMont.year}"
            }
        }
        binding.exFiveCalendar.monthScrollListener = {
            // to show toast on top of selected month
            val strTitle =
                "${it.yearMonth.month.name.toLowerCase().capitalize()} ${it.yearMonth.year}"
            Log.d("SAN", "strTitle-->$strTitle")
           // binding.exFiveCalendar.legendLayout.tv_month.text = strTitle
            // viewModel.setStateEvent(PlannerStateEvent.GetMonthlyPlannerData(""))
        }

    }
}