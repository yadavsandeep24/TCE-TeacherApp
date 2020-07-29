package com.tce.teacherapp.ui.dashboard.planner

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.CalendarDayMarkCompletedBinding
import com.tce.teacherapp.databinding.FragmentMarkCompletedBinding
import com.tce.teacherapp.db.entity.LessonPlanPeriod
import com.tce.teacherapp.db.entity.LessonPlanResource
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.planner.adapter.lessonPlanEpoxyHolder
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.util.calenderView.utils.yearMonth
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
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


@FlowPreview
@ExperimentalCoroutinesApi
class MarkCompletedFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BasePlannerFragment(R.layout.fragment_mark_completed, viewModelFactory), LessonPlanClickListener {

    private lateinit var binding: FragmentMarkCompletedBinding
    private lateinit var tvDone: TextView
    private lateinit var tvCount: TextView

    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    private lateinit var  sharedPreference : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMarkCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference =  requireActivity().getSharedPreferences("MARK_COUNT", Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
        editor.putInt("mark_count",0).apply()

        val lessonPlanPeriod = arguments?.getParcelable("lessonPlanData") as LessonPlanPeriod?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.planner_event_top_bar)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        val topBar =
            (activity as DashboardActivity).binding.toolBar.findViewById<RelativeLayout>(R.id.event_top_container)

        val tvBack =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tv_back) as TextView)
        val toolbarTitle =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.toolbar_title) as TextView)
        tvDone =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tvDone) as TextView)
        tvCount =
            ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tvCount) as TextView)


        tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }

        toolbarTitle.text = "Mark Completed"
        topBar.setBackgroundColor(Color.parseColor("#83ba42"))
        binding.tvSelectedDate.text = SimpleDateFormat("dd MMMM yyyy EEEE").format(Date())

        val linearLayoutManager3 = LinearLayoutManager(binding.rvResource.context)
        linearLayoutManager3.orientation = LinearLayoutManager.VERTICAL
        binding.rvResource.apply {
            layoutManager = linearLayoutManager3
            setHasFixedSize(true)
        }

        binding.rvResource.withModels {
            lessonPlanPeriod.let {
                lessonPlanEpoxyHolder {
                    id(it!!.id)
                    period(it)
                    screenType("markCompleted")
                    lessonPLanClickListener(this@MarkCompletedFragment)
                }
            }
        }

        tvDone.setOnClickListener(View.OnClickListener {
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

            val count : Int  = sharedPreference.getInt("mark_count",0)

            txtTitle.text = "$count Resources Marked!"

            Handler().postDelayed({
                dialog.dismiss()
                (activity as DashboardActivity).onBackPressed()
            }, 1000)

        })



        binding.dateContainer.setOnClickListener {
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
                        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy EEEE")
                        val formattedString: String = day.date.format(formatter)

                        binding.tvSelectedDate.text = formattedString
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

    override fun onResume() {
        super.onResume()

    }
    override fun onLessonPlanClick(lessonPlanPeriod: LessonPlanPeriod) {
        TODO("Not yet implemented")
    }

    override fun onMarkCompletedClick(lessonPlanPeriod: LessonPlanPeriod) {
        TODO("Not yet implemented")
    }

    override fun onResourceMarkCompletedChecked(resource: LessonPlanResource, isChecked : Boolean) {
        if(isChecked){
            var count : Int  = sharedPreference.getInt("mark_count",0)
            editor.putInt("mark_count",count + 1)
            editor.commit()
           }else{
            var count : Int  = sharedPreference.getInt("mark_count",0)
            editor.putInt("mark_count",count - 1)
            editor.commit()
        }

        var count : Int  = sharedPreference.getInt("mark_count",0)
        if(count > 0){
            tvDone.visibility = View.VISIBLE
            tvCount.visibility = View.VISIBLE
            tvCount.setText(count.toString())
        }  else{
            tvDone.visibility = View.GONE
            tvCount.visibility = View.GONE
        }
    }

    override fun onLessonPlanResourceItemClick(resource: LessonPlanResource) {
    }

}