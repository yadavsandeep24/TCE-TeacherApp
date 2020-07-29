package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.dashboard.planner.listeners.EventClickListener
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.planner_event_list_item)
abstract class LessonParentEventEpoxyHolder : EpoxyModelWithHolder<LessonParentEventHolder>() {

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

    @EpoxyAttribute
    lateinit var imageDrawable : Drawable

    @EpoxyAttribute
    lateinit var imageUrl: String

    @EpoxyAttribute
    lateinit var evenClickListener : EventClickListener

    @EpoxyAttribute
    lateinit var event: Event


    override fun bind(holder: LessonParentEventHolder) {
        super.bind(holder)


        holder.tvEvent.setText(strEvent)

        holder.tvEvent.maxLines = 1
        holder.tvReadMore.setText(Html.fromHtml("<u>Read More</u>"))
        holder.tvReadMore.setOnClickListener(View.OnClickListener {
            if(holder.tvReadMore.text.toString().equals("Read More", ignoreCase = true)){
                holder.tvReadMore.setText(Html.fromHtml("<u>Read Less</u>"))
                holder.tvEvent.maxLines = 5
            }else{
                holder.tvReadMore.setText(Html.fromHtml("<u>Read More</u>"))
                holder.tvEvent.maxLines = 1
            }
        })

        holder.tvEventType.setText(strEventType)
        holder.imgIcon.background = imageDrawable
        holder.tvEvent.setTextColor(Color.parseColor(eventColor))
        holder.tvReadMore.setTextColor(Color.parseColor(eventColor))
        holder.tvEventType.setTextColor(Color.parseColor(typeColor))
        holder.eventContainer.setBackgroundColor(Color.parseColor(eventBackColor))
        holder.iconContainer.setBackgroundColor(Color.parseColor(iconBackColor))
        holder.eventContainer.setOnClickListener{evenClickListener.onEventItemClick(event)}
        Utility.setSelectorRoundedCornerWithDynamicColor(
            holder.eventContainer!!.context,  holder.eventContainer!!, 0,
            eventBackColor, iconBackColor,
            R.color.transparent, R.color.transparent, 0
        )

    }

}

class LessonParentEventHolder : KotlinEpoxyHolder(){
    val tvEvent by bind<TextView>(R.id.tvEventName)
    val eventContainer by bind<RelativeLayout>(R.id.event_container)
    val iconContainer by bind<LinearLayout>(R.id.icon_container)
    val tvEventType by bind<TextView>(R.id.tv_event_type)
    val tvReadMore by bind<TextView>(R.id.tvReadMore)
    val imgIcon by bind<AppCompatImageView>(R.id.img_icon)
}
