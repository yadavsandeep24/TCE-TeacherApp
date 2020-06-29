package com.tce.teacherapp.ui.home.adapter

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.LatestUpdateSubList
import com.tce.teacherapp.ui.dashboard.home.adapter.eventItemEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.adapter.todayResChildItemEpoxyHolder
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.list_item_latest_update)
abstract class LatestUpdateChildItemEpoxyHolder : EpoxyModelWithHolder<ViewedChildHolder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    lateinit var subTitle: String

    @EpoxyAttribute
    lateinit var strIcon: String

    @EpoxyAttribute
    lateinit var imageDrawable: Drawable

    @EpoxyAttribute
    lateinit var subList: LatestUpdateSubList


    override fun bind(holder: ViewedChildHolder) {

        holder.tvTitle.setText(title)
        holder.tvSubTitle.setText(subTitle)
        holder.imgIcon.background = imageDrawable

        if (subList.attendance == null && subList.resourceList == null && subList.eventList == null) {
            holder.sublistContainer.visibility = View.GONE
            holder.attendanceContainer.visibility = View.GONE
            holder.resourceContainer.visibility = View.GONE
            holder.eventContainer.visibility = View.GONE
        } else {
            holder.sublistContainer.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(subList.attendance) ){
                holder.attendanceContainer.visibility = View.VISIBLE
                holder.resourceContainer.visibility = View.GONE
                holder.eventContainer.visibility = View.GONE

                holder.tvAttendance.setText(subList.attendance)

            } else if (subList.resourceList.size > 0) {
                holder.attendanceContainer.visibility = View.GONE
                holder.resourceContainer.visibility = View.VISIBLE
                holder.eventContainer.visibility = View.GONE

                holder.rvResource.layoutManager = GridLayoutManager(holder.rvResource.context, 2)
                holder.rvResource.setHasFixedSize(false)
                val epoxyVisibilityTracker = EpoxyVisibilityTracker()
                epoxyVisibilityTracker.attach(holder.rvResource)

                holder.rvResource.withModels {
                    subList.resourceList.let {
                        for (type in it) {
                            todayResChildItemEpoxyHolder {
                                id(type.id)
                                strName(type.title)
                                strIcon(type.icon)
                                strImageUrl(type.imageUrl)
                                strVideoUrl(type.videoUrl)
                                Utility.getDrawable(
                                    type.icon.substring(
                                        0,
                                        type.icon.lastIndexOf(".")
                                    ), holder.rvResource.context
                                )?.let { it1 ->
                                    imageDrawable(it1)
                                }
                            }
                        }

                    }
                }

            } else if (subList.eventList.size > 0) {
                holder.attendanceContainer.visibility = View.GONE
                holder.resourceContainer.visibility = View.GONE
                holder.eventContainer.visibility = View.VISIBLE

                holder.rvEvent.layoutManager = GridLayoutManager(holder.rvEvent.context, 1)
                holder.rvEvent.setHasFixedSize(false)
                val epoxyVisibilityTracker = EpoxyVisibilityTracker()
                epoxyVisibilityTracker.attach(holder.rvEvent)

                holder.rvEvent.withModels {
                    subList.eventList.let {
                        for(event in it){
                            eventItemEpoxyHolder {
                                id(event.id)
                                strEvent(event.eventName)
                                strEventType(event.type)
                                eventColor(event.eventColor)
                                typeColor(event.typeColor)
                                eventBackColor(event.eventBackColor)
                                iconBackColor(event.iconBackColor)
                                imageUrl(event.imageUrl)
                                Utility.getDrawable(
                                    event.imageUrl.substring(
                                        0,
                                        event.imageUrl.lastIndexOf(".")
                                    ), holder.rvEvent.context
                                )?.let { it1 ->
                                    imageDrawable(it1)
                                }
                            }
                        }
                    }
                }

            }
        }

    }
}

class ViewedChildHolder : KotlinEpoxyHolder() {
    val imgIcon by bind<AppCompatImageView>(R.id.location_icon)
    val tvTitle by bind<TextView>(R.id.tv_title)
    val tvSubTitle by bind<TextView>(R.id.tv_sub_title)
    val sublistContainer by bind<LinearLayout>(R.id.sub_list_container)
    val tvAttendance by bind<TextView>(R.id.tv_attendance)
    val attendanceContainer by bind<LinearLayout>(R.id.attendance_container)
    val resourceContainer by bind<LinearLayout>(R.id.resource_container)
    val eventContainer by bind<LinearLayout>(R.id.event_container)
    val rvResource by bind<EpoxyRecyclerView>(R.id.rv_resource)
    val tvViewPlanner by bind<TextView>(R.id.tvViewAll)
    val rvEvent by bind<EpoxyRecyclerView>(R.id.rv_event)


}