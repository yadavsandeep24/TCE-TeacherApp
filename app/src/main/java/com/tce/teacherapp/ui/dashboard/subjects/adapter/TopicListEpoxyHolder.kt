package com.tce.teacherapp.ui.dashboard.subjects.adapter

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.list_item_topic)
abstract class TopicListEpoxyHolder : EpoxyModelWithHolder<TopicListHolder>() {

    @EpoxyAttribute
    lateinit var listener: () -> Unit
    @EpoxyAttribute
    lateinit var title: String
    @EpoxyAttribute
    lateinit var imageUrl: String
    @EpoxyAttribute
    lateinit var imageDrawable: Drawable

    override fun bind(holder: TopicListHolder) {
        holder.tvTitle.text = title
        holder.cardMain.setOnClickListener { listener() }
        holder.imageView.background = imageDrawable
        //holder.glide.loadImage(imageUrl).into(holder.imageView)
    }
}

class TopicListHolder : KotlinEpoxyHolder() {
    val cardMain by bind<com.google.android.material.card.MaterialCardView>(R.id.card_topic)
    val tvTitle by bind<TextView>(R.id.tv_topic_title)
    val imageView by bind<AppCompatImageView>(R.id.iv_topic)
    val glide by lazy { Glide.with(imageView.context) }
}
