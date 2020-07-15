package com.tce.teacherapp.ui.dashboard.planner

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.CalendarDayBinding
import com.tce.teacherapp.databinding.CalendarHeaderBinding
import com.tce.teacherapp.databinding.FragmentMonthlyPlannerBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.planner.adapter.dailyPlannerEpoxyHolder
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
import org.jetbrains.anko.childrenSequence
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MonthlyPlannerFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BasePlannerFragment(R.layout.fragment_monthly_planner, viewModelFactory) {

    private lateinit var binding : FragmentMonthlyPlannerBinding
    private var selectedDate: LocalDate? = null

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

     //   viewModel.setStateEvent(PlannerStateEvent.GetMonthlyPlannerData("2020-07-15"))

        binding.dailyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background = null
            binding.dailyContainer.background =  resources.getDrawable(R.drawable.bg_lessaon_daily_left)
            binding.tvDaily.setTextColor(resources.getColor(R.color.white))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.blue))
            binding.imgDaily.background =  resources.getDrawable(R.drawable.ic_day)
            binding.imgMonthly.background =  resources.getDrawable(R.drawable.ic_month_blue)
            (activity as DashboardActivity).onBackPressed()

        })

        binding.monthlyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background = resources.getDrawable(R.drawable.bg_lessaon_daily_right)
            binding.dailyContainer.background = null
            binding.tvDaily.setTextColor(resources.getColor(R.color.blue))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.white))
            binding.imgDaily.background =  resources.getDrawable(R.drawable.ic_day_blue)
            binding.imgMonthly.background =  resources.getDrawable(R.drawable.ic_month)
            findNavController().navigate(
                R.id.action_plannerFragment_to_monthlyPlannerFragment
            )
        })

       val daysOfWeek = daysOfWeekFromLocale()

        val currentMonth = YearMonth.now()
        binding.exFiveCalendar.setup(currentMonth, currentMonth, daysOfWeek.first())
        binding.exFiveCalendar.scrollToMonth(currentMonth)

       class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding =  CalendarDayBinding.bind(view)
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            val binding = this@MonthlyPlannerFragment.binding
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }
                            //updateAdapterForDate(day.date)
                        }
                    }
                }
            }
        }
        binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                viewModel.setStateEvent(PlannerStateEvent.GetMonthlyPlannerData(day.date.toString()))

                val textView = container.binding.exFiveDayText
                val layout = container.binding.exFiveDayLayout
                textView.text = day.date.dayOfMonth.toString()

                val rvEvent = container.binding.rvEvent

                val linearLayoutManager1 = LinearLayoutManager(rvEvent.context)
                linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
                rvEvent.apply {
                    layoutManager = linearLayoutManager1
                    setHasFixedSize(true)
                }

                viewModel.viewState.observe(viewLifecycleOwner, Observer{viewState ->
                    if(viewState != null){
                        viewState.monthlyPlannerList?.let {
                            for(plan in it) {
                                rvEvent.withModels {
                                    for (event in plan.eventList) {
                                        monthlyEventEpoxyHolder {
                                            id(event.id)
                                            strEvent(event.eventName)
                                            strEventType(event.type)
                                            eventColor(event.eventColor)
                                            typeColor(event.typeColor)
                                            eventBackColor(event.eventBackColor)
                                            iconBackColor(event.iconBackColor)
                                        }
                                    }
                                }
                            }
                        }
                    }

                })

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.setTextColorRes(R.color.calender_text_grey)
                    layout.setBackgroundResource(if (selectedDate == day.date) R.drawable.calender_selected_bg else 0)
                } else {
                    textView.setTextColorRes(R.color.calender_text_grey_light)
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        binding.exFiveCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            @SuppressLint("NewApi")
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.day_view.childrenSequence().map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .toUpperCase(Locale.ENGLISH)
                        tv.setTextColorRes(R.color.calender_text_grey)
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    }

                    month.yearMonth
                    container.legendLayout.tv_month.text = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"

                }
            }
        }



    }




}