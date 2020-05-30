package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_message)
abstract class MessageListEpoxyHolder:EpoxyModelWithHolder<Holder>() {

    /*@EpoxyAttribute
    lateinit var imageDrawable : Drawable*/
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
    lateinit var listener: () -> Unit

    override fun bind(holder: Holder) {
        holder.tvMessage.text = strMessage
        holder.tvDetail.text = strDetail
       // holder.imageView.background = imageDrawable
        //  holder.glide.loadImage(imageUrl).into(holder.imageView)
        holder.tvTime.text = strTime
        holder.tvCount.text = strCount
        holder.mainContainer.setOnClickListener { listener() }
    }
}

class Holder : KotlinEpoxyHolder(){
    val tvMessage by bind<TextView>(R.id.tvMessage)
    val tvDetail by bind<TextView>(R.id.tvDetail)
    val tvTime by bind<TextView>(R.id.tvTime)
    val tvCount by bind<TextView>(R.id.tvCount)
    val imageView by bind<AppCompatImageView>(R.id.imgMessage)
    val mainContainer by bind<RelativeLayout>(R.id.main_container)
}