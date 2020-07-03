package com.tce.teacherapp.ui.home.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.ui.home.listeners.LatestUpdateClickListeners
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.dashboard_event_list_item)
abstract class EventItemEpoxyHolder : EpoxyModelWithHolder<ItemHolder>() {

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
    lateinit var latestUpdateClickListeners: LatestUpdateClickListeners

    @EpoxyAttribute
    lateinit var event: Event

    override fun bind(holder: ItemHolder) {
        holder.tvEvent.text = strEvent
        holder.tvEventType.text = strEventType
        holder.imgIcon.background = imageDrawable
        holder.tvEvent.setTextColor(Color.parseColor(eventColor))
        holder.tvEventType.setTextColor(Color.parseColor(typeColor))
        holder.eventContainer.setBackgroundColor(Color.parseColor(eventBackColor))
        holder.iconContainer.setBackgroundColor(Color.parseColor(iconBackColor))
        holder.eventContainer.setOnClickListener{latestUpdateClickListeners.onLatestUpdateEventClick(event)}
        Utility.setSelectorRoundedCornerWithDynamicColor(
            holder.eventContainer.context,  holder.eventContainer, 0,
            eventBackColor, iconBackColor,
            R.color.transparent, R.color.transparent, 0
        )

        holder.tvReadMore.text = Html.fromHtml("<u>Read More</u>")
        holder.tvReadMore.setTextColor(Color.parseColor(eventColor))
    }
}

class ItemHolder : KotlinEpoxyHolder(){
    val tvEvent by bind<TextView>(R.id.tvEventName)
    val eventContainer by bind<LinearLayout>(R.id.event_container)
    val iconContainer by bind<LinearLayout>(R.id.icon_container)
    val tvEventType by bind<TextView>(R.id.tv_event_type)
    val imgIcon by bind<AppCompatImageView>(R.id.img_icon)
    val tvReadMore by bind<TextView>(R.id.tvReadMore)

}