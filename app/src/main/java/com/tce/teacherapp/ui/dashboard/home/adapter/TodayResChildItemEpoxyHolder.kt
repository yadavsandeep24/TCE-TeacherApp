package com.tce.teacherapp.ui.dashboard.home.adapter

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_today_res_child_item)
abstract class TodayResChildItemEpoxyHolder :EpoxyModelWithHolder<TodayChildHolder>(){

    @EpoxyAttribute
    lateinit var strName:String

    override fun bind(holder: TodayChildHolder) {

        holder.tvTitle.setText(strName)
       
    }


}

class TodayChildHolder : KotlinEpoxyHolder(){
    val tvTitle by bind<TextView>(R.id.tv_subject_name)
}