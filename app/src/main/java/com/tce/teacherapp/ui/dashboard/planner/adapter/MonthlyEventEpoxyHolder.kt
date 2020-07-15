package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.planner_month_event_list_item)
abstract class MonthlyEventEpoxyHolder : EpoxyModelWithHolder<MonthlyEventHolder>() {

    @EpoxyAttribute
    lateinit var strEvent : String
    @EpoxyAttribute
    lateinit var strEventType : String
    @EpoxyAttribute
    lateinit var eventColor : String
    @EpoxyAttribute
    lateinit var typeColor : String
    @EpoxyAttribute
    lateinit var eventBackColor : String
    @EpoxyAttribute
    lateinit var iconBackColor : String


    override fun bind(holder: MonthlyEventHolder) {
        super.bind(holder)

        holder.tvEvent.setText(strEvent)
        holder.tvEvent.setTextColor(Color.parseColor(eventColor))
        holder.eventContainer.setBackgroundColor(Color.parseColor(eventBackColor))


    }
}

class MonthlyEventHolder : KotlinEpoxyHolder(){
    val tvEvent by bind<TextView>(R.id.tv_event)
    val eventContainer by bind<LinearLayout>(R.id.event_container)
}