package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.EpoxyRecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.DashboardResourceType
import com.tce.teacherapp.db.entity.LessonPlanPeriod
import com.tce.teacherapp.db.entity.LessonPlanResource
import com.tce.teacherapp.ui.dashboard.home.adapter.todayResChildItemEpoxyHolder
import com.tce.teacherapp.ui.dashboard.home.listeners.ClassListClickListener
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.list_item_lesson_plan)
abstract class LessonPlanEpoxyHolder : EpoxyModelWithHolder<PlanHolder>() {

    @EpoxyAttribute
    lateinit var period:LessonPlanPeriod

    @EpoxyAttribute
    lateinit var lessonPLanClickListener: LessonPlanClickListener

    override fun bind(holder: PlanHolder) {
        super.bind(holder)

        holder.tvSequence.setText(period.SequenceNo.toString())
        holder.tvTitle.text = period.Topic
        holder.tvSubTitle.text = period.LessonTypeValue
        val linearLayoutManager = LinearLayoutManager(holder.rvResourceItem.context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.rvResourceItem.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)

        }

        holder.lessonPlanContainer.setOnClickListener(View.OnClickListener {
            lessonPLanClickListener.onLessonPlanClick(period)
        })

        holder.rvResourceItem.withModels {
            period.ResourceList?.let {
                for (type in it){
                    lessonPlanChildItemEpoxyHolder {
                        id(type.id)
                        resource(type)
                        Utility.getDrawable(
                            type.image.substring(
                                0,
                                type.image.lastIndexOf(".")
                            ), holder.rvResourceItem.context
                        )?.let { it1 ->
                            imageDrawable(it1)
                        }
                    }

                }
            }


        }
    }
}

class PlanHolder : KotlinEpoxyHolder(){
    val tvSequence by bind<TextView>(R.id.tv_sequence)
    val tvTitle by bind<TextView>(R.id.tv_res_title)
    val tvSubTitle by bind<TextView>(R.id.tv_res_sub_title)
    val rvResourceItem by bind<EpoxyRecyclerView>(R.id.rv_resource_item)
    val lessonPlanContainer by bind<LinearLayout>(R.id.lesson_plan_container)
}