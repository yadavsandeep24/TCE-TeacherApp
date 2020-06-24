package com.tce.teacherapp.ui.dashboard.home.adapter

import android.text.Html
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.DashboardResource
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_today_resources)
abstract class TodayResourceEpoxyHolder : EpoxyModelWithHolder<ResourceHolder>() {

    @EpoxyAttribute
    lateinit var strTitle:String

    @EpoxyAttribute
    lateinit var resourceList:ArrayList<DashboardResource>

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: ResourceHolder) {

        holder.tvShowMore.text = Html.fromHtml("<u>Show More (6)</u>")
        holder.tvShowMore.setOnClickListener{listener()}

        holder.tvTitle.text = strTitle
        holder.rvResource.layoutManager = GridLayoutManager(holder.rvResource.context, 1)
        holder.rvResource.setHasFixedSize(false)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(holder.rvResource)

        holder.rvResource.withModels {
            resourceList?.let {
                for(res in it) {
                    todayResourceItemEpoxyHolder {
                        id(res.resId)
                        title(res.title)
                        subTitle(res.subTitle)
                        resourceTypeList(res.typeList)
                    }
                }
            }

        }
    }

}

class ResourceHolder : KotlinEpoxyHolder(){

    val tvShowMore by bind<TextView>(R.id.tvShowMore)
    val tvTitle by bind<TextView>(R.id.title)
    val rvResource by bind<EpoxyRecyclerView>(R.id.rv_dash_resource)
}