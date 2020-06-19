package com.tce.teacherapp.ui.dashboard.home.adapter

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_viewed_res_child_item)
abstract class ViewedResChildItemEpoxyHolder : EpoxyModelWithHolder<ViewedChildHolder>(){

    @EpoxyAttribute
    lateinit var strName:String

    override fun bind(holder: ViewedChildHolder) {

        holder.tvTitle.setText(strName)

    }
}

class ViewedChildHolder : KotlinEpoxyHolder(){
    val tvTitle by bind<TextView>(R.id.tv_subject_name)
}