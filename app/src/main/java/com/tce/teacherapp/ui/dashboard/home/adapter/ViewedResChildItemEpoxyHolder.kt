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
import com.tce.teacherapp.db.entity.DashboardResourceType
import com.tce.teacherapp.ui.dashboard.home.listeners.LastViewedResourceClickListener
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_viewed_res_child_item)
abstract class ViewedResChildItemEpoxyHolder : EpoxyModelWithHolder<ViewedChildHolder>(){

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
    lateinit var lastViewedClickListener : LastViewedResourceClickListener

    @EpoxyAttribute
    lateinit var dashboardResourceType: DashboardResourceType

    override fun bind(holder: ViewedChildHolder) {

        holder.tvTitle.setText(strName)
        holder.imgIcon.background = imageDrawable
        if(!TextUtils.isEmpty(strVideoUrl)){
            holder.imgPlay.visibility = View.VISIBLE
        }else{
            holder.imgPlay.visibility = View.GONE
        }

        holder.mainContainer.setOnClickListener{lastViewedClickListener.onLastViewedItemClick(dashboardResourceType)}
    }
}

class ViewedChildHolder : KotlinEpoxyHolder(){
    val imgIcon by bind<AppCompatImageView>(R.id.img_icon)
    val imgPlay by bind<AppCompatImageView>(R.id.img_play)
    val tvTitle by bind<TextView>(R.id.tv_subject_name)
    val mainContainer by bind<RelativeLayout>(R.id.main_container)
}