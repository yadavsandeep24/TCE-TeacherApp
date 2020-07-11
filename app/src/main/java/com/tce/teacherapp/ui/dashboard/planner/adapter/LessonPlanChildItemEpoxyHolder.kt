package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.LessonPlanResource
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.list_item_child_lesson_plan)
abstract class LessonPlanChildItemEpoxyHolder : EpoxyModelWithHolder<PlanChildHolder>() {


    @EpoxyAttribute
    lateinit var imageDrawable : Drawable

    @EpoxyAttribute
    lateinit var resource:LessonPlanResource

    override fun bind(holder: PlanChildHolder) {
        super.bind(holder)
       // holder.mainContainer.setOnClickListener{todayResourceClickListener.onTodayResourceItemClick(dashboardResourceType)}
        Utility.setSelectorRoundedCorner(
            holder.mainContainer!!.context,  holder.mainContainer!!, 0,
            R.color.transparent, R.color.dim_color,
            R.color.transparent, R.color.transparent, 0
        )

        holder.tvTitle.setText(resource.title)
        holder.imgIcon.background = imageDrawable
        if(resource.contenttype.equals("AV")){
            holder.imgPlay.visibility = View.VISIBLE
        }else{
            holder.imgPlay.visibility = View.GONE
        }

    }

}

class PlanChildHolder : KotlinEpoxyHolder(){
    val imgIcon by bind<AppCompatImageView>(R.id.img_icon)
    val imgPlay by bind<AppCompatImageView>(R.id.img_play)
    val tvTitle by bind<TextView>(R.id.tv_subject_name)
    val mainContainer by bind<RelativeLayout>(R.id.main_container)
}