package com.tce.teacherapp.ui.dashboard.home.adapter

import android.text.Html
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.DashboardResourceType
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_viewed_resources)
abstract class ViewedResourceEpoxyHolder : EpoxyModelWithHolder<ViewedHolder>() {

    @EpoxyAttribute
    lateinit var strTitle:String

    @EpoxyAttribute
    lateinit var resourceList:ArrayList<DashboardResourceType>

    override fun bind(holder: ViewedHolder) {

        holder.tvTitle.setText(strTitle)
        holder.tvShowMore.setText(Html.fromHtml("<u>Show More (10)</u>"))
        holder.rvResource.layoutManager = GridLayoutManager(holder.rvResource.context, 2)
        holder.rvResource.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(holder.rvResource)

        holder.rvResource.withModels {
            resourceList?.let {
                for(res in it){
                    viewedResChildItemEpoxyHolder {
                        id(res.id)
                        strName(res.title)
                    }
                }
            }

        }
    }

}

class ViewedHolder : KotlinEpoxyHolder(){
    val tvShowMore by bind<TextView>(R.id.tvShowMore)
    val tvTitle by bind<TextView>(R.id.view_title)
    val rvResource by bind<EpoxyRecyclerView>(R.id.rv_dash_view_resource)
}