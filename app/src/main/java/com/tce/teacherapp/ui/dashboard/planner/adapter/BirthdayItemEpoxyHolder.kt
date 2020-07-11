package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder


@EpoxyModelClass(layout = R.layout.list_item_birthday)
abstract class BirthdayItemEpoxyHolder : EpoxyModelWithHolder<BirthdayHolder>(){
    @EpoxyAttribute
    lateinit var name : String

    override fun bind(holder: BirthdayHolder) {
        super.bind(holder)
        holder.tvName.setText(name)
    }
}

class BirthdayHolder : KotlinEpoxyHolder(){

    val tvName by bind<TextView>(R.id.tvName)
}