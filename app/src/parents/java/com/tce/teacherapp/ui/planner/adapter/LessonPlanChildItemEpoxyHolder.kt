package com.tce.teacherapp.ui.planner.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.LessonPlanResource
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.list_item_child_lesson_plan)
abstract class LessonPlanChildItemEpoxyHolder : EpoxyModelWithHolder<PlanChildHolder>() {

    @EpoxyAttribute
    lateinit var screenType:String

    @EpoxyAttribute
    lateinit var imageDrawable : Drawable

    @EpoxyAttribute
    lateinit var resource:LessonPlanResource

    @EpoxyAttribute
    lateinit var lessonPLanClickListener: LessonPlanClickListener

    override fun bind(holder: PlanChildHolder) {
        super.bind(holder)
       // holder.mainContainer.setOnClickListener{todayResourceClickListener.onTodayResourceItemClick(dashboardResourceType)}
        Utility.setSelectorRoundedCorner(
            holder.mainContainer!!.context,  holder.mainContainer!!, 0,
            R.color.transparent, R.color.dim_color,
            R.color.transparent, R.color.transparent, 0
        )

        if(screenType.equals("markCompleted")){
            holder.imgPlay.visibility = View.GONE
        }else{
            holder.imgPlay.visibility = View.VISIBLE
            if(resource.contenttype.equals("AV")){
                holder.imgPlay.visibility = View.VISIBLE
            }else{
                holder.imgPlay.visibility = View.GONE
            }
        }


        holder.tvTitle.setText(resource.title)
        holder.imgIcon.background = imageDrawable




    }

}

class PlanChildHolder : KotlinEpoxyHolder(){
    val imgIcon by bind<AppCompatImageView>(R.id.img_icon)
    val imgPlay by bind<AppCompatImageView>(R.id.img_play)
    val tvTitle by bind<TextView>(R.id.tv_subject_name)
    val mainContainer by bind<RelativeLayout>(R.id.main_container)
}