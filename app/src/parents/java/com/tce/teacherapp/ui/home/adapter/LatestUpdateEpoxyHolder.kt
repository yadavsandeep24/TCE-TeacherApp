package com.tce.teacherapp.ui.home.adapter

import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.*
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.DashboardLatestUpdate
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.dashboard_latest_update)
abstract class LatestUpdateEpoxyHolder : EpoxyModelWithHolder<ViewedHolder>() {

    @EpoxyAttribute
    lateinit var strTitle:String



    @EpoxyAttribute
    lateinit var latestUpdateList:ArrayList<DashboardLatestUpdate>

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: ViewedHolder) {

        holder.tvTitle.text = strTitle


        holder.rvResource.layoutManager = GridLayoutManager(holder.rvResource.context, 1)
        holder.rvResource.setHasFixedSize(false)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(holder.rvResource)

        holder.rvResource.withModels {
            latestUpdateList?.let {
                for(res in it){
                    latestUpdateChildItemEpoxyHolder {
                        id(res.id)
                        title(res.title)
                        subTitle(res.subtitle)
                        strIcon(res.icon)
                        subList(res.subList)
                        Utility.getDrawable(
                            res.icon.substring(
                                0,
                                res.icon.lastIndexOf(".")
                            ), holder.rvResource.context
                        )?.let { it1 ->
                            imageDrawable(it1)
                        }

                        listener {
                            Toast.makeText(holder.rvResource.context, "Click Message" , Toast.LENGTH_LONG).show()
                        }

                        listenerPlanner {
                            Toast.makeText(holder.rvResource.context, "Click Planner" , Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }

        }
    }
}

class ViewedHolder : KotlinEpoxyHolder(){

    val tvTitle by bind<TextView>(R.id.view_title)
    val rvResource by bind<EpoxyRecyclerView>(R.id.rv_dash_view_resource)
}