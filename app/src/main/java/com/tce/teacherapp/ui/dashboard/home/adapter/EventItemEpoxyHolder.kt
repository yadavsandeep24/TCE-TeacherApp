package com.tce.teacherapp.ui.dashboard.home.adapter

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_event_list_item)
abstract class EventItemEpoxyHolder : EpoxyModelWithHolder<ItemHolder>() {

    @EpoxyAttribute
    lateinit var strEvent : String

    override fun bind(holder: ItemHolder) {
        holder.tvEvent.setText(strEvent)
    }
}

class ItemHolder : KotlinEpoxyHolder(){
    val tvEvent by bind<TextView>(R.id.tvEventName)
}