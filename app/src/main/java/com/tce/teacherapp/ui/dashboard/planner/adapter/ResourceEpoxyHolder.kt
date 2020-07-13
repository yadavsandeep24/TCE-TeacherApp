package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_lesson_plan_resources)
abstract class ResourceEpoxyHolder : EpoxyModelWithHolder<LessonResourceHolder>() {

    @EpoxyAttribute
    lateinit var name : String
    override fun bind(holder: LessonResourceHolder) {
        super.bind(holder)
        holder.tvName.setText(name)
    }
}

class LessonResourceHolder : KotlinEpoxyHolder(){

    val tvName by bind<TextView>(R.id.tv_res_name)
}
