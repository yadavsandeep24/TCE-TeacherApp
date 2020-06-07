package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_attachment)
abstract class AttachmentEpoxyHolder : EpoxyModelWithHolder<AttachmentHolder>(){

    @EpoxyAttribute
    lateinit var strThumb : String
    @EpoxyAttribute
    lateinit var strPath : String
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: AttachmentHolder) {
        Glide.with(holder.imgThumb.context).load("file://" + strThumb)
            .skipMemoryCache(false)
            .into(holder.imgThumb)
        holder.rlSelect.setOnClickListener { listener() }
    }
}

class AttachmentHolder : KotlinEpoxyHolder(){
    val rlSelect by bind<MaterialCardView>(R.id.rl_select)
    val imgThumb by bind<ImageView>(R.id.iv_image)
    val glide by lazy { Glide.with(imgThumb.context) }
}