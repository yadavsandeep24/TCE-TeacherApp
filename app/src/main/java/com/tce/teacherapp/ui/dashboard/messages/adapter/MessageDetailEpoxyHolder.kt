package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Resource
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@EpoxyModelClass(layout = R.layout.list_item_message_details)
abstract class MessageDetailEpoxyHolder : EpoxyModelWithHolder<MessageDetailEpoxyHolder.Holder>() {

    @EpoxyAttribute
    lateinit var strMessage: String

    @EpoxyAttribute
    lateinit var messageType: String

    @EpoxyAttribute
    lateinit var resourceList: List<Resource>

    @EpoxyAttribute
    lateinit var strDateTime: String

    @EpoxyAttribute
    lateinit var strIsRead: String

    @EpoxyAttribute
    lateinit var listener: ISelectedResourceClickListener

    override fun bind(holder: Holder) {
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date: Date = format.parse(strDateTime)
        val datestring: String = SimpleDateFormat("dd MMMM yyyy").format(date)
        holder.tvDate.text = datestring
        val timeString: String = SimpleDateFormat("hh:mm aa").format(date)
        holder.tvTimeUserName.text = timeString
        holder.tvRightTimeUserName.text = timeString
        if (strDateTime.equals("0", false)) {
            holder.vw_tick.backgroundTintList =
                holder.vw_tick.context.resources.getColorStateList(R.color.blue_grey)
        } else {
            holder.vw_tick.backgroundTintList =
                holder.vw_tick.context.resources.getColorStateList(R.color.turquoise)
        }

        if (messageType.equals("2", false)) {
            holder.rlLeftMessageContainer.visibility = View.VISIBLE
            holder.rlRightMessageContainer.visibility = View.GONE
            holder.tvDetail.text = strMessage
            holder.cardVideo.visibility = View.GONE
            holder.cardImage.visibility = View.GONE
            holder.ivUser.visibility = View.GONE
            val timeString: String = SimpleDateFormat("hh:mm aa").format(date)
            holder.tvTimeUserName.text = timeString
        } else {
            holder.rlLeftMessageContainer.visibility = View.GONE
            holder.rlRightMessageContainer.visibility = View.VISIBLE
            if (strMessage.isEmpty()) {
                holder.vw_tick.visibility = View.GONE
                holder.tvRightTimeUserName.visibility = View.GONE
                holder.tvRightMessage.visibility = View.GONE
            } else {
                holder.vw_tick.visibility = View.VISIBLE
                holder.tvRightTimeUserName.visibility = View.VISIBLE
                holder.tvRightMessage.visibility = View.VISIBLE
                holder.tvRightMessage.text = strMessage
            }
            holder.ivUser.visibility = View.VISIBLE
        }

        for (message in resourceList) {
            if (message.contenttype.equals("av", true)) {
                if (TextUtils.isEmpty(message.src)) {
                    holder.cardVideo.visibility = View.GONE
                } else {
                    holder.cardVideo.visibility = View.VISIBLE
                    holder.cardVideo.setOnClickListener {
                        listener.onResourceSelected(message)
                    }
                    Glide.with(holder.cardVideo.context)
                        .load(message.image)
                        .placeholder(R.drawable.dummy_video)
                        .into(holder.ivVideothumb)
                    holder.tvVideoName.text = message.title
                }
            }
            if (message.contenttype.equals("image", true)) {
                if (TextUtils.isEmpty(message.src)) {
                    holder.cardImage.visibility = View.GONE
                } else {
                    holder.cardImage.visibility = View.VISIBLE
                    holder.cardImage.setOnClickListener {
                        listener.onResourceSelected(message)
                    }
                    Glide.with(holder.cardImage.context)
                        .load(message.src)
                        .placeholder(R.drawable.dummy_image)
                        .into(holder.ivImage)
                }
            }
        }


    }

    class Holder : KotlinEpoxyHolder() {
        val tvDetail by bind<TextView>(R.id.tvDetail)
        val tvDate by bind<TextView>(R.id.tv_date)
        val tvTimeUserName by bind<TextView>(R.id.tv_time_user_name)
        val tvRightTimeUserName by bind<TextView>(R.id.tv_right_time_user_name)
        val tvVideoName by bind<TextView>(R.id.tv_video_name)
        val vw_tick by bind<View>(R.id.vw_tick)
        val ivUser by bind<AppCompatImageView>(R.id.img_user)
        val ivImage by bind<AppCompatImageView>(R.id.img_image)
        val ivVideothumb by bind<AppCompatImageView>(R.id.img_video_thumb)
        val tvRightMessage by bind<TextView>(R.id.tvRightDetail)
        val rlLeftMessageContainer by bind<RelativeLayout>(R.id.left_message_container)
        val rlRightMessageContainer by bind<RelativeLayout>(R.id.right_message_container)
        val cardVideo by bind<com.google.android.material.card.MaterialCardView>(R.id.card_video)
        val cardImage by bind<com.google.android.material.card.MaterialCardView>(R.id.card_image)
    }
}

interface ISelectedResourceClickListener {
    fun onResourceSelected(item: Resource)
}
