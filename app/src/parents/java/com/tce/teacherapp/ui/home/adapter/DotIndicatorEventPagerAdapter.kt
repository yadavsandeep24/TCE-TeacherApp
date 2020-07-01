package com.tce.teacherapp.ui.home.adapter

import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.home.listeners.EventClickListener
import com.tce.teacherapp.util.Utility
import java.util.*

class DotIndicatorEventPagerAdapter(private val eventListener: EventClickListener) :
    RecyclerView.Adapter<DotIndicatorEventPagerAdapter.ViewHolder>() {
    internal var mEventList: List<Event>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_event_list_item, parent, false)
        return ViewHolder(v)
    }

    fun setData(list: ArrayList<Event>) {
        this.mEventList = list
    }

    override fun getItemCount(): Int {
        return if (mEventList != null) {
            mEventList!!.size
        } else {
            0
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvEventType: TextView
        internal var tvEventName: TextView
        internal var tvReadMore: TextView
        internal var imgIcon: AppCompatImageView
        internal var iconContainer: LinearLayout
        internal var eventContainer: LinearLayout
        internal var mainContainer: LinearLayout

        init {
            tvEventType = itemView.findViewById(R.id.tv_event_type)
            tvEventName = itemView.findViewById(R.id.tvEventName)
            tvReadMore = itemView.findViewById(R.id.tvReadMore)
            imgIcon = itemView.findViewById(R.id.img_icon)
            iconContainer = itemView.findViewById(R.id.icon_container)
            eventContainer = itemView.findViewById(R.id.event_container)
            mainContainer = itemView.findViewById(R.id.main_container)

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvEventName.setText(mEventList!!.get(position).eventName)
        holder.tvReadMore.setText(Html.fromHtml("<u>Read More</u>"))
        holder.tvReadMore.setTextColor(Color.parseColor(mEventList!!.get(position).eventColor))

        holder.tvEventType.setText(mEventList!!.get(position).type)
        Utility.getDrawable(
            mEventList!!.get(position).imageUrl.substring(
                0,
                mEventList!!.get(position).imageUrl.lastIndexOf(".")
            ), holder.imgIcon.context
        )?.let { it1 ->
            holder.imgIcon.background = it1
        }
        holder.tvEventName.setTextColor(Color.parseColor(mEventList!!.get(position).eventColor))
        holder.tvEventType.setTextColor(Color.parseColor(mEventList!!.get(position).typeColor))
        holder.eventContainer.setBackgroundColor(Color.parseColor(mEventList!!.get(position).eventBackColor))
        holder.iconContainer.setBackgroundColor(Color.parseColor(mEventList!!.get(position).iconBackColor))

        Utility.setSelectorRoundedCornerWithDynamicColor(
            holder.eventContainer!!.context,  holder.eventContainer!!, 0,
            mEventList!!.get(position).eventBackColor, mEventList!!.get(position).iconBackColor,
            R.color.transparent, R.color.transparent, 0
        )

        holder.eventContainer.setOnClickListener(View.OnClickListener {
            eventListener.onEventListItemClick(mEventList!!.get(position).type)
        })
    }
}
