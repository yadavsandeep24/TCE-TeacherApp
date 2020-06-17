package com.tce.teacherapp.ui.dashboard.home.adapter

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_today_resources)
abstract class TodayResourceEpoxyHolder : EpoxyModelWithHolder<ResourceHolder>() {

    @EpoxyAttribute
    lateinit var strTitle:String

    override fun bind(holder: ResourceHolder) {

        holder.tvTitle.setText(strTitle)
        holder.rvResource.layoutManager = GridLayoutManager(holder.rvResource.context, 1)
        holder.rvResource.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(holder.rvResource)

        holder.rvResource.withModels {
            for (i in 0 until 2) {
               todayResourceItemEpoxyHolder {
                    id(i)
               }
            }
        }
    }

}

class ResourceHolder : KotlinEpoxyHolder(){
    val tvTitle by bind<TextView>(R.id.title)
    val rvResource by bind<EpoxyRecyclerView>(R.id.rv_dash_resource)
}