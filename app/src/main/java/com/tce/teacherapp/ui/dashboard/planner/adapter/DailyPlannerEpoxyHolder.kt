package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.EpoxyRecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.DailyPlanner
import com.tce.teacherapp.ui.dashboard.home.listeners.EventClickListener
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility
import java.text.SimpleDateFormat
import java.util.*

@EpoxyModelClass(layout = R.layout.list_item_planner)
abstract class DailyPlannerEpoxyHolder : EpoxyModelWithHolder<PlannerHolder>() {

    @EpoxyAttribute
    lateinit var dailyPlanner: DailyPlanner

    @EpoxyAttribute
    lateinit var evenClickListener : EventClickListener

    @EpoxyAttribute
    lateinit var lessonPLanClickListener: LessonPlanClickListener


    override fun bind(holder: PlannerHolder) {
        super.bind(holder)
        holder.tvDate.setText(dailyPlanner.date)

       /* if(showLessButton){
            holder.tvShowMore.text = Html.fromHtml("<u>Show Less</u>")
            holder.ivShowMore.background = holder.rvEvent.context.resources.getDrawable(R.drawable.ic_line)
        }else {
            holder.tvShowMore.text = Html.fromHtml("<u>Show More ($nextEventCount)</u>")
            holder.ivShowMore.background = holder.rvEvent.context.resources.getDrawable(R.drawable.ic_add)

        }
        holder.tvShowMore.setOnClickListener{evenClickListener.onEventShowMoreClick(showLessButton)}*/


        if(SimpleDateFormat("dd MMMM yyyy EEEE").format(Date()).equals(dailyPlanner.date, ignoreCase = true)){
            holder.tvDate.visibility = View.GONE
        }else{
            holder.tvDate.visibility = View.VISIBLE
        }

        val linearLayoutManager1 = LinearLayoutManager(holder.rvEvent.context)
        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
        holder.rvEvent.apply {
            layoutManager = linearLayoutManager1
            setHasFixedSize(true)
        }

        holder.rvEvent.withModels {
            dailyPlanner.eventList?.let {
                for (event in it){
                    lessonEventEpoxyHolder {
                        id(event.id)
                        strEvent(event.eventName)
                        strEventType(event.type)
                        eventColor(event.eventColor)
                        typeColor(event.typeColor)
                        eventBackColor(event.eventBackColor)
                        iconBackColor(event.iconBackColor)
                        imageUrl(event.imageUrl)
                        Utility.getDrawable(
                            event.imageUrl.substring(
                                0,
                                event.imageUrl.lastIndexOf(".")
                            ), holder.rvEvent.context
                        )?.let { it1 ->
                            imageDrawable(it1)
                        }
                        event(event)
                        evenClickListener(evenClickListener)
                    }

                }
            }


        }


        val linearLayoutManager2 = LinearLayoutManager(holder.rvBirthday.context)
        linearLayoutManager2.orientation = LinearLayoutManager.HORIZONTAL
        holder.rvBirthday.apply {
            layoutManager = linearLayoutManager2
            setHasFixedSize(true)
        }

        holder.rvBirthday.withModels {
            dailyPlanner.birthdayList?.let {
                for (event in it){
                    birthdayItemEpoxyHolder {
                        id(event.name)
                        name(event.name)
                    }

                }
            }


        }

        val linearLayoutManager3 = LinearLayoutManager(holder.rvLessonPLan.context)
        linearLayoutManager3.orientation = LinearLayoutManager.VERTICAL
        holder.rvLessonPLan.apply {
            layoutManager = linearLayoutManager3
            setHasFixedSize(true)
        }

        holder.rvLessonPLan.withModels {
            dailyPlanner.lessonPlan?.let {
                for (event in dailyPlanner.lessonPlan.PeriodList){
                    lessonPlanEpoxyHolder {
                        id(event.id)
                        period(event)
                        lessonPLanClickListener(lessonPLanClickListener)
                    }
                }
            }
        }

    }
}

class PlannerHolder : KotlinEpoxyHolder(){
    val tvShowMore by bind<TextView>(R.id.tvShowMore)
    val ivShowMore by bind<ImageView>(R.id.iv_indicator)
    val tvDate by bind<TextView>(R.id.tv_date)
    val rvEvent by bind<EpoxyRecyclerView>(R.id.rv_event)
    val rvBirthday by bind<EpoxyRecyclerView>(R.id.rv_birthday)
    val rvLessonPLan by bind<EpoxyRecyclerView>(R.id.rv_lessons)
}