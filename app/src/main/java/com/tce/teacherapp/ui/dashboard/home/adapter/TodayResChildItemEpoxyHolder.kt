package com.tce.teacherapp.ui.dashboard.home.adapter

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
import com.tce.teacherapp.ui.dashboard.home.listeners.TodayResourceClickListener
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.dashboard_today_res_child_item)
abstract class TodayResChildItemEpoxyHolder :EpoxyModelWithHolder<TodayChildHolder>(){

    @EpoxyAttribute
    lateinit var strName:String

    @EpoxyAttribute
    lateinit var strVideoUrl:String

    @EpoxyAttribute
    lateinit var strImageUrl:String

    @EpoxyAttribute
    lateinit var strIcon:String

    @EpoxyAttribute
    lateinit var imageDrawable : Drawable

    @EpoxyAttribute
    lateinit var todayResourceClickListener : TodayResourceClickListener


    override fun bind(holder: TodayChildHolder) {

        holder.mainContainer.setOnClickListener{todayResourceClickListener.onTodayResourceItemClick(strName)}
        Utility.setSelectorRoundedCorner(
            holder.mainContainer!!.context,  holder.mainContainer!!, 0,
            R.color.transparent, R.color.dim_color,
            R.color.transparent, R.color.transparent, 0
        )

        holder.tvTitle.setText(strName)
        holder.imgIcon.background = imageDrawable
        if(!TextUtils.isEmpty(strVideoUrl)){
            holder.imgPlay.visibility = View.VISIBLE
        }else{
            holder.imgPlay.visibility = View.GONE
        }

    }


}

class TodayChildHolder : KotlinEpoxyHolder(){
    val imgIcon by bind<AppCompatImageView>(R.id.img_icon)
    val imgPlay by bind<AppCompatImageView>(R.id.img_play)
    val tvTitle by bind<TextView>(R.id.tv_subject_name)
    val mainContainer by bind<RelativeLayout>(R.id.main_container)
}