package com.tce.teacherapp.ui.dashboard.home.adapter

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.dashboard.messages.adapter.StudentHolder
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_header)
abstract class HeaderEpoxyHolder : EpoxyModelWithHolder<HeaderHolder>() {

    @EpoxyAttribute
    lateinit var strName : String

    override fun bind(holder: HeaderHolder) {

        holder.tvUserName.text = strName
    }
}

class HeaderHolder : KotlinEpoxyHolder(){
    val tvUserName by bind<TextView>(R.id.tvName)
}