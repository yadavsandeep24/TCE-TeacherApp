package com.tce.teacherapp.ui.dashboard.home.adapter

import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.dashboard_event)
abstract class EventEpoxyHolder :EpoxyModelWithHolder<EventHolder>() {

    @EpoxyAttribute
    lateinit var strDate : String

    @EpoxyAttribute
    lateinit var eventList : List<Event>

    @EpoxyAttribute
    var nextEventCount : Int =4

    @EpoxyAttribute
    var showLessButton : Boolean = false

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: EventHolder) {

        holder.tvDate.text = strDate
        if(showLessButton){
            holder.tvShowMore.text = Html.fromHtml("<u>Show Less</u>")
            holder.ivShowMore.background = holder.rvEvent.context.resources.getDrawable(R.drawable.ic_line)
        }else {
            holder.tvShowMore.text = Html.fromHtml("<u>Show More ($nextEventCount)</u>")
            holder.ivShowMore.background = holder.rvEvent.context.resources.getDrawable(R.drawable.ic_add)

        }
        holder.tvShowMore.setOnClickListener{listener()}

        holder.rvEvent.layoutManager = GridLayoutManager(holder.rvEvent.context, 1)
        holder.rvEvent.setHasFixedSize(false)
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
                        listener {
                            Toast.makeText(holder.rvEvent.context, "Click" + event.type, Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }



        }

    }
}

class EventHolder : KotlinEpoxyHolder(){
    val tvShowMore by bind<TextView>(R.id.tvShowMore)
    val ivShowMore by bind<ImageView>(R.id.iv_indicator)
    val tvDate by bind<TextView>(R.id.tvDate)
    val rvEvent by bind<EpoxyRecyclerView>(R.id.rv_event)
}