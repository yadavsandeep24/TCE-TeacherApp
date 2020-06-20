package com.tce.teacherapp.ui.dashboard.home.adapter

import android.text.Html
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.dashboard.home.DashboardViewModel
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.dashboard_event)
abstract class EventEpoxyHolder :EpoxyModelWithHolder<EventHolder>() {

    @EpoxyAttribute
    lateinit var strDate : String

    @EpoxyAttribute
    lateinit var eventList : ArrayList<Event>

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: EventHolder) {

        holder.tvDate.text = strDate
        holder.tvShowMore.setText(Html.fromHtml("<u>Show More (4)</u>"))
        holder.tvShowMore.setOnClickListener{listener()}

        holder.rvEvent.layoutManager = GridLayoutManager(holder.rvEvent.context, 1)
        holder.rvEvent.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(holder.rvEvent)

        holder.rvEvent.withModels {
            eventList.let {
                for(event in it){
                    eventItemEpoxyHolder {
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
                    }
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