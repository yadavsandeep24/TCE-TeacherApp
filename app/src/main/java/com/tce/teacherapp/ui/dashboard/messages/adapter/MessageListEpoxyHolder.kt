package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@EpoxyModelClass(layout = R.layout.list_item_message)
abstract class MessageListEpoxyHolder:EpoxyModelWithHolder<Holder1>() {


    @EpoxyAttribute
    lateinit var strMessage : String
    @EpoxyAttribute
    lateinit var strDetail : String
    @EpoxyAttribute
    lateinit var strTime : String
    @EpoxyAttribute
    lateinit var strCount : String

    @EpoxyAttribute
    lateinit var imageUrl: String

    @EpoxyAttribute
    lateinit var messageType: String

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: Holder1) {
        holder.tvMessage.text = strMessage
        if(strDetail.isEmpty()){
            holder.tvDetail.visibility = View.GONE
        }else{
            holder.tvDetail.visibility = View.VISIBLE
        holder.tvDetail.text = strDetail
        }
        when {
            messageType.equals("1", false) -> {
                holder.imageView.background = holder.imageView.context.getDrawable(R.drawable.ic_profile_icon)
            }
            messageType.equals("2", false) -> {
                holder.imageView.background = holder.imageView.context.getDrawable(R.drawable.icon_dummy_school)
            }
            messageType.equals("3", false) -> {
                holder.imageView.background = holder.imageView.context.getDrawable(R.drawable.ic_dummy_class)
            }
        }

        //  holder.glide.loadImage(imageUrl).into(holder.imageView)
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(strTime)
        val newstring: String = SimpleDateFormat("hh:mm aa").format(date)
        holder.tvTime.text = newstring
        holder.tvCount.text = strCount
        holder.mainContainer.setOnClickListener { listener() }
    }
}

class Holder1 : KotlinEpoxyHolder(){
    val tvMessage by bind<TextView>(R.id.tvMessage)
    val tvDetail by bind<TextView>(R.id.tvDetail)
    val tvTime by bind<TextView>(R.id.tvTime)
    val tvCount by bind<TextView>(R.id.tvCount)
    val imageView by bind<AppCompatImageView>(R.id.imgMessage)
    val mainContainer by bind<RelativeLayout>(R.id.main_container)
}