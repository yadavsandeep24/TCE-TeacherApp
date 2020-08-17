package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_message_details)
abstract class MessageDetailEpoxyHolder:EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute
    lateinit var strDetail : String

    @EpoxyAttribute
    lateinit var strVideoUrl : String

    @EpoxyAttribute
    lateinit var strImageUrl : String

    @EpoxyAttribute
    lateinit var strMessageType : String

    override fun bind(holder: Holder) {
        if(strMessageType.equals("1")) {
            holder.rlLeftMessageContainer.visibility = View.VISIBLE
            holder.rlRightMessageContainer.visibility = View.GONE
            holder.tvDetail.text = strDetail
        }else{
            holder.rlLeftMessageContainer.visibility = View.GONE
            holder.rlRightMessageContainer.visibility = View.VISIBLE
            holder.tvRightMessage.text = strDetail
        }

        if(TextUtils.isEmpty(strVideoUrl)) {
            holder.cardVideo.visibility = View.GONE
        }else{
            holder.cardVideo.visibility = View.VISIBLE
        }

        if(TextUtils.isEmpty(strImageUrl)){
            holder.cardImage.visibility = View.GONE
        }else{
            holder.cardImage.visibility = View.VISIBLE
        }
    }
}

class Holder : KotlinEpoxyHolder() {
    val tvDetail by bind<TextView>(R.id.tvDetail)
    val tvRightMessage by bind<TextView>(R.id.tvRightDetail)
    val rlLeftMessageContainer by bind<RelativeLayout> (R.id.left_message_container)
    val rlRightMessageContainer by bind<RelativeLayout> (R.id.right_message_container)
    val cardVideo by bind<com.google.android.material.card.MaterialCardView>(R.id.card_video)
    val cardImage by bind<com.google.android.material.card.MaterialCardView>(R.id.card_image)
}