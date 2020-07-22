package com.tce.teacherapp.ui.dashboard.planner.adapter

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.DailyPlanner
import com.tce.teacherapp.ui.dashboard.home.listeners.EventClickListener
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.util.Constants
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.progress_loading.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DailyPlannerRvAdapter(private var itemsCells: ArrayList<DailyPlanner>,
                           private var  evenClickListener: EventClickListener,
                           private var lessonPLanClickListener: LessonPlanClickListener
) : RecyclerView.Adapter<DailyPlannerRvAdapter.ItemViewHolder>() {


    lateinit var mcontext: Context

    class ItemViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView){
        internal var tvShowMore: TextView
        internal var ivShowMore: ImageView
        internal var tvDate: TextView
        internal var rvEvent: EpoxyRecyclerView
        internal var rvBirthday: EpoxyRecyclerView
        internal var rvLessonPLan: EpoxyRecyclerView
        internal var markCompletedContainer: LinearLayout
        internal var progressLoading: LinearLayout
        internal var dataContainer: LinearLayout

        init {
            tvShowMore = itemView.findViewById(R.id.tvShowMore)
            ivShowMore = itemView.findViewById(R.id.iv_indicator)
            tvDate = itemView.findViewById(R.id.tv_date)
            rvEvent = itemView.findViewById(R.id.rv_event)
            rvBirthday = itemView.findViewById(R.id.rv_birthday)
            rvLessonPLan = itemView.findViewById(R.id.rv_lessons)
            markCompletedContainer = itemView.findViewById(R.id.mark_completed_container)
            progressLoading = itemView.findViewById(R.id.progress_loading)
            dataContainer = itemView.findViewById(R.id.data_container)
            if (viewType == Constants.VIEW_TYPE_ITEM) {
                dataContainer.visibility = View.VISIBLE
                progressLoading.visibility = View.GONE
            }else{
                progressLoading.visibility = View.VISIBLE
            }
        }


    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addData(dataViews: ArrayList<DailyPlanner>) {
        this.itemsCells.addAll(dataViews)
        notifyDataSetChanged()
    }

    fun getItemAtPosition(position: Int): DailyPlanner {
        return itemsCells[position]
    }

    fun addLoadingView() {
        //add loading item
        Handler().post {
           // itemsCells.add(null)
            notifyItemInserted(itemsCells.size - 1)
        }
    }

    fun removeLoadingView() {
        //Remove loading item
        if (itemsCells.size != 0) {
            itemsCells.removeAt(itemsCells.size - 1)
            notifyItemRemoved(itemsCells.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        mcontext = parent.context
       // return if (viewType == Constants.VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_planner, parent, false)
           return ItemViewHolder(view, viewType)
       /* } else {
            val view = LayoutInflater.from(mcontext).inflate(R.layout.progress_loading, parent, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.progressbar.indeterminateDrawable.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
            } else {
                view.progressbar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            }
            ItemViewHolder(view)
        }*/
    }

    override fun getItemCount(): Int {
        return itemsCells.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemsCells[position] == null) {
            Constants.VIEW_TYPE_LOADING
        } else {
            Constants.VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (holder.itemViewType == Constants.VIEW_TYPE_ITEM) {
            var dailyPlanner = itemsCells[position]
            holder.tvDate.setText(dailyPlanner.date)

            var nextEventCount = dailyPlanner.eventData.nextEventCount

            if(dailyPlanner.eventData.isShowLess){
                holder.tvShowMore.text = Html.fromHtml("<u>Show Less</u>")
                holder.ivShowMore.background = holder.rvEvent.context.resources.getDrawable(R.drawable.ic_line)
            }else {
                holder.tvShowMore.text = Html.fromHtml("<u>Show More ($nextEventCount)</u>")
                holder.ivShowMore.background = holder.rvEvent.context.resources.getDrawable(R.drawable.ic_add)

            }
            holder.tvShowMore.setOnClickListener{evenClickListener.onEventShowMoreClick(dailyPlanner.eventData.isShowLess)}

            holder.markCompletedContainer.setOnClickListener(View.OnClickListener {
                lessonPLanClickListener.onMarkCompletedClick(dailyPlanner.lessonPlan.PeriodList.get(0))
            })

            if(SimpleDateFormat("dd MMMM yyyy EEEE").format(Date()).equals(dailyPlanner.date, ignoreCase = true)){
                holder.tvDate.visibility = View.GONE
            }else{
                holder.tvDate.visibility = View.VISIBLE
            }

            val linearLayoutManager1 = LinearLayoutManager(holder.rvEvent.context)
            linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
            holder.rvEvent.apply {
                layoutManager = linearLayoutManager1
                setHasFixedSize(true)
            }

            holder.rvEvent.withModels {
                dailyPlanner.eventData.eventList?.let {
                    for (event in it){
                        lessonEventEpoxyHolder {
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
                            event(event)
                            evenClickListener(evenClickListener)
                        }

                    }
                }


            }


            val linearLayoutManager2 = LinearLayoutManager(holder.rvBirthday.context)
            linearLayoutManager2.orientation = LinearLayoutManager.HORIZONTAL
            holder.rvBirthday.apply {
                layoutManager = linearLayoutManager2
                setHasFixedSize(true)
            }

            holder.rvBirthday.withModels {
                dailyPlanner.birthdayList?.let {
                    for (event in it){
                        birthdayItemEpoxyHolder {
                            id(event.name)
                            name(event.name)
                        }

                    }
                }


            }

            val linearLayoutManager3 = LinearLayoutManager(holder.rvLessonPLan.context)
            linearLayoutManager3.orientation = LinearLayoutManager.VERTICAL
            holder.rvLessonPLan.apply {
                layoutManager = linearLayoutManager3
                setHasFixedSize(true)
            }

            holder.rvLessonPLan.withModels {
                dailyPlanner.lessonPlan?.let {
                    for (event in dailyPlanner.lessonPlan.PeriodList){
                        lessonPlanEpoxyHolder {
                            id(event.id)
                            period(event)
                            screenType("lessonPlan")
                            lessonPLanClickListener(lessonPLanClickListener)
                        }
                    }
                }
            }

        }
    }


}