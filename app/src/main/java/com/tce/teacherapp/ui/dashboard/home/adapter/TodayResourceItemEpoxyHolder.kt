package com.tce.teacherapp.ui.dashboard.home.adapter

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_today_resource_item)
abstract class TodayResourceItemEpoxyHolder : EpoxyModelWithHolder<ResourceItemHolder>() {


    override fun bind(holder: ResourceItemHolder) {

        val linearLayoutManager = LinearLayoutManager(holder.rvResourceItem.context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.rvResourceItem.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)

        }

        holder.rvResourceItem.withModels {
            for (i in 0 until 2) {
               todayResChildItemEpoxyHolder {
                   id(i)
                   strName("Title Name 1")
               }
            }
        }
    }


}

class ResourceItemHolder : KotlinEpoxyHolder(){
    val tvTitle by bind<TextView>(R.id.tv_res_title)
    val rvResourceItem by bind<EpoxyRecyclerView>(R.id.rv_resource_item)
}