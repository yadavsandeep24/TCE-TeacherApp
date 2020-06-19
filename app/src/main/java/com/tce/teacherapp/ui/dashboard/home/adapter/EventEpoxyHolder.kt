package com.tce.teacherapp.ui.dashboard.home.adapter

import android.text.Html
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_event)
abstract class EventEpoxyHolder :EpoxyModelWithHolder<EventHolder>() {

    @EpoxyAttribute
    lateinit var strDate : String

    override fun bind(holder: EventHolder) {

        holder.tvDate.text = strDate
        holder.tvShowMore.setText(Html.fromHtml("<u>Show More (4)</u>"))
        holder.rvEvent.layoutManager = GridLayoutManager(holder.rvEvent.context, 1)
        holder.rvEvent.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(holder.rvEvent)

        holder.rvEvent.withModels {
            for (i in 0 until 3) {
                eventItemEpoxyHolder {
                    id(i)
                    strEvent("School Sports Day 2020")
                }
            }
        }

    }
}

class EventHolder : KotlinEpoxyHolder(){
    val tvShowMore by bind<TextView>(R.id.tvShowMore)
    val tvDate by bind<TextView>(R.id.tvDate)
    val rvEvent by bind<EpoxyRecyclerView>(R.id.rv_event)
}