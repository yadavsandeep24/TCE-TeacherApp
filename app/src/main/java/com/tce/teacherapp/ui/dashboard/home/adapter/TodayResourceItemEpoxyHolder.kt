package com.tce.teacherapp.ui.dashboard.home.adapter

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.EpoxyRecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.DashboardResourceType
import com.tce.teacherapp.ui.dashboard.home.listeners.TodayResourceClickListener
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.dashboard_today_resource_item)
abstract class TodayResourceItemEpoxyHolder : EpoxyModelWithHolder<ResourceItemHolder>() {

    @EpoxyAttribute
    lateinit var title : String

    @EpoxyAttribute
    lateinit var subTitle : String

    @EpoxyAttribute
    lateinit var resourceTypeList:ArrayList<DashboardResourceType>

    @EpoxyAttribute
    lateinit var todayResourceClickListener : TodayResourceClickListener

    override fun bind(holder: ResourceItemHolder) {

        holder.tvTitle.text = title
        holder.tvSubTitle.text = subTitle
        val linearLayoutManager = LinearLayoutManager(holder.rvResourceItem.context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.rvResourceItem.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)

        }

        holder.rvResourceItem.withModels {
            resourceTypeList?.let {
                for (type in it){
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
                            ), holder.rvResourceItem.context
                        )?.let { it1 ->
                            imageDrawable(it1)
                        }
                        dashboardResourceType(type)
                        todayResourceClickListener(todayResourceClickListener)
                    }
                }
            }


        }
    }


}

class ResourceItemHolder : KotlinEpoxyHolder(){
    val tvTitle by bind<TextView>(R.id.tv_res_title)
    val tvSubTitle by bind<TextView>(R.id.tv_res_sub_title)
    val rvResourceItem by bind<EpoxyRecyclerView>(R.id.rv_resource_item)
}