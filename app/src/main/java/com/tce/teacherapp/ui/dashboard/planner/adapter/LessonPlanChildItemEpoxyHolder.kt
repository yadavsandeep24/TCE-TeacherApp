package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
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

    lateinit var resource:LessonPlanResource
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    @EpoxyAttribute
    lateinit var lessonPLanClickListener: LessonPlanClickListener

    override fun bind(holder: PlanChildHolder) {
        super.bind(holder)
       // holder.mainContainer.setOnClickListener{todayResourceClickListener.onTodayResourceItemClick(dashboardResourceType)}
        Utility.setSelectorRoundedCorner(
            holder.mainContainer.context,  holder.mainContainer!!, 0,
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


        holder.tvTitle.text = resource.title

        when {
            resource.contenttype.equals("av", true) -> {
                holder.imgPlay.visibility = View.VISIBLE
                holder.imgIcon.background =
                    holder.mainContainer.context.resources.getDrawable(R.drawable.ic_video_icon)
            }
            resource.contenttype.equals("Image", true) -> {
                holder.imgPlay.visibility = View.GONE
                holder.imgIcon.background =
                    holder.mainContainer.context.resources.getDrawable(R.drawable.ic_media_type_image)
            }
            resource.contenttype.equals("activity", true) -> {
                holder.imgPlay.visibility = View.GONE
                holder.imgIcon.background =
                    holder.mainContainer.context.resources.getDrawable(R.drawable.ic_media_type_actvity)
            }
            resource.contenttype.equals("audio", true) -> {
                holder.imgPlay.visibility = View.VISIBLE
                holder.imgIcon.background =
                    holder.mainContainer.context.resources.getDrawable(R.drawable.ic_media_type_audio)
            }
            resource.contenttype.equals("ebook", true) -> {
                holder.imgPlay.visibility = View.VISIBLE
                holder.imgIcon.background =
                    holder.mainContainer.context.resources.getDrawable(R.drawable.ic_media_type_doc)
            }
            else ->{

            }
        }
        Glide.with(holder.imgThumb.context)
            .load(resource.image)
            .placeholder(R.drawable.dummy_video)
            .into(holder.imgThumb)

        if(screenType.equals("markCompleted")) {
            holder.chkContainer.visibility = View.VISIBLE
            holder.chkCompleted.setOnCheckedChangeListener { buttonView, isChecked ->
                lessonPLanClickListener.onResourceMarkCompletedChecked(resource, isChecked)
                if (isChecked) {
                    holder.chkContainer.background =
                        holder.chkContainer.context.resources.getDrawable(R.drawable.green_left_bottom_rounded)
                } else {
                    holder.chkContainer.background =
                        holder.chkContainer.context.resources.getDrawable(R.drawable.grey_left_bottom_rounded)
                }
            }
        }else{
            holder.chkContainer.visibility = View.GONE
        }
        holder.imgThumb.setOnClickListener {
            lessonPLanClickListener.onLessonPlanResourceItemClick(resource)
        }

    }

}

class PlanChildHolder : KotlinEpoxyHolder(){
    val imgIcon by bind<AppCompatImageView>(R.id.img_icon)
    val imgPlay by bind<AppCompatImageView>(R.id.img_play)
    val tvTitle by bind<TextView>(R.id.tv_subject_name)
    val mainContainer by bind<RelativeLayout>(R.id.main_container)
    val chkCompleted by bind<CheckBox>(R.id.chk_completed)
    val chkContainer by bind<LinearLayout>(R.id.checkbox_container)
    val imgThumb by bind<AppCompatImageView>(R.id.img_video_thumb)
}