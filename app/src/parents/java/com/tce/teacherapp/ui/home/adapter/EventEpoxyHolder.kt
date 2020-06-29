package com.tce.teacherapp.ui.home.adapter

import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.dotsIndicator.ZoomOutPageTransformer
import java.util.*

@EpoxyModelClass(layout = R.layout.dashboard_event)
abstract class EventEpoxyHolder :EpoxyModelWithHolder<EventHolder>() {

    @EpoxyAttribute
    lateinit var strDate : String

    @EpoxyAttribute
    lateinit var eventList : List<Event>

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: EventHolder) {

        holder.tvDate.text = strDate


        val adapter = DotIndicatorEventPagerAdapter()
        adapter.setData(eventList as ArrayList<Event>)
        holder.viewPagerEvent.adapter = adapter

        val zoomOutPageTransformer =  ZoomOutPageTransformer()
        holder.viewPagerEvent.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }

        holder.dotsIndicator.setViewPager2(holder.viewPagerEvent)


    }
}

class EventHolder : KotlinEpoxyHolder(){
    val tvDate by bind<TextView>(R.id.tvDate)
    val viewPagerEvent by bind<ViewPager2>(R.id.view_pager_event)
    val dotsIndicator by bind<SpringDotsIndicator>(R.id.dots_indicator)
}