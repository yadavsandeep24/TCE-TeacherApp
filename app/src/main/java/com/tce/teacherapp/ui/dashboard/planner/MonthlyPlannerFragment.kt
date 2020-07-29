package com.tce.teacherapp.ui.dashboard.planner

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.CalendarDayBinding
import com.tce.teacherapp.databinding.CalendarHeaderBinding
import com.tce.teacherapp.databinding.FragmentMonthlyPlannerBinding
import com.tce.teacherapp.ui.dashboard.planner.adapter.monthlyEventEpoxyHolder
import com.tce.teacherapp.ui.dashboard.planner.state.PLANNER_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerStateEvent
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
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
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class MonthlyPlannerFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BasePlannerFragment(R.layout.fragment_monthly_planner, viewModelFactory) {

    private lateinit var binding: FragmentMonthlyPlannerBinding
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "PlannerViewState: inState is NOT null")
            (inState[PLANNER_VIEW_STATE_BUNDLE_KEY] as PlannerViewState?)?.let { viewState ->
                Log.d(TAG, "PlannerViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMonthlyPlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setStateEvent(PlannerStateEvent.GetMonthlyPlannerData(""))

        binding.dailyContainer.setOnClickListener {
            navigateToDailyFragment(null)
        }

        if (resources.getString(R.string.app_type)
                .equals(resources.getString(R.string.app_type_parent), true)
        ) {
            binding.tvToday.visibility = View.GONE
            binding.imgStudent.visibility = View.VISIBLE
        }

        val daysOfWeek = daysOfWeekFromLocale()

        val currentYearMonth = YearMonth.now()
        Log.d("SAN", "currentYearMonth-->$currentYearMonth")
        binding.exFiveCalendar.setup(
            currentYearMonth.minusMonths(11),
            currentYearMonth.plusMonths(11),
            daysOfWeek.first()
        )
        binding.exFiveCalendar.scrollToMonth(currentYearMonth)
        binding.exFiveCalendar.monthScrollListener = {
            // to show toast on top of selected month
            val strTitle =
                "${it.yearMonth.month.name.toLowerCase().capitalize()} ${it.yearMonth.year}"
            Log.d("SAN", "strTitle-->$strTitle")
            binding.exFiveCalendar.legendLayout.tv_month.text = strTitle
            viewModel.setStateEvent(PlannerStateEvent.GetMonthlyPlannerData(""))
        }
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    Log.d("SAN", "Calender_Click-->")
                    if (day.owner == DayOwner.THIS_MONTH) {
                        navigateToDailyFragment(day.date.toString())
                    }
                }
            }
        }
        binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day

                val textView = container.binding.exFiveDayText
                val layout = container.binding.exFiveDayLayout
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

                    val rvEvent = container.binding.rvEvent

                    val linearLayoutManager1 = LinearLayoutManager(rvEvent.context)
                    linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
                    rvEvent.apply {
                        layoutManager = linearLayoutManager1
                        setHasFixedSize(false)
                    }

                    viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
                        if (viewState != null) {
                            viewState.monthlyPlannerList?.let { lstMonthlyPlanner ->
                                if (lstMonthlyPlanner.isNotEmpty()) {
                                    val dayPlanList = lstMonthlyPlanner.filter {
                                        val formatter =
                                            DateTimeFormatter.ofPattern("dd MMMM yyyy EEEE")
                                        val formattedString: String = day.date.format(formatter)
                                        Log.d("SAN", "formattedString-->$formattedString")
                                        it.date.contains(
                                            formattedString,
                                            ignoreCase = true
                                        )
                                    }
                                    if (dayPlanList.isNotEmpty()) {
                                        for (plan in dayPlanList) {
                                            rvEvent.withModels {
                                                for (event in plan.eventTypeList) {
                                                    monthlyEventEpoxyHolder {
                                                        id(event.id)
                                                        strEvent(event.eventName)
                                                        strEventType(event.type)
                                                        eventColor(event.eventColor)
                                                        typeColor(event.typeColor)
                                                        eventBackColor(event.eventBackColor)
                                                        iconBackColor(event.iconBackColor)
                                                        eventCount(event.eventCount)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
                } else {
                    textView.setTextColorRes(R.color.calender_text_grey_light)
                    textView.background = null
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
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    /*        container.legendLayout.day_view.childrenSequence().map { it as TextView }.forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                    .toUpperCase(Locale.ENGLISH)
                                tv.setTextColorRes(R.color.calender_text_grey)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            }*/
                    month.yearMonth
                    container.legendLayout.tv_month.text =
                        "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"

                } else {
                    val yearMont = container.legendLayout.tag as YearMonth
                    container.legendLayout.tv_month.text =
                        "${yearMont.month.name.toLowerCase().capitalize()} ${yearMont.year}"
                }
            }
        }
        binding.tvToday.setOnClickListener {
            navigateToDailyFragment(null)
        }
    }

    private fun navigateToDailyFragment(date: String?) {
        binding.monthlyContainer.background =
            resources.getDrawable(R.drawable.bg_lessaon_daily_right)
        binding.dailyContainer.background = null
        binding.tvDaily.setTextColor(resources.getColor(R.color.blue))
        binding.tvMonthly.setTextColor(resources.getColor(R.color.white))
        binding.imgDaily.background = resources.getDrawable(R.drawable.ic_day_blue)
        binding.imgMonthly.background = resources.getDrawable(R.drawable.ic_month)
        val bundle = Bundle()
        bundle.putString("date", date)
        findNavController().navigate(R.id.action_monthlyPlannerFragment_to_plannerFragment, bundle)
    }
}