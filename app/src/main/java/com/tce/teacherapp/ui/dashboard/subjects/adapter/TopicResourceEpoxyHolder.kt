package com.tce.teacherapp.ui.dashboard.subjects.adapter

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Resource
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.list_item_topic_resource)
abstract class TopicResourceEpoxyHolder : EpoxyModelWithHolder<TopicResourceHolder>() {

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    @EpoxyAttribute
    lateinit var resourceVo: Resource

    override fun bind(holder: TopicResourceHolder) {
        holder.cardMain.setOnClickListener { listener() }
        holder.tvTitle.text = resourceVo.title
        holder.tvSubTitle.text = resourceVo.TaggingLevel
        Glide.with(holder.cardMain.context)
            .load(resourceVo.image)
            .placeholder(R.drawable.topic_resource_bg)
            .into(holder.ivBackground)
        when {
            resourceVo.contenttype.equals("av", true) -> {
                holder.imgPlay.visibility = View.VISIBLE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_video)
            }
            resourceVo.contenttype.equals("Image", true) -> {
                holder.imgPlay.visibility = View.GONE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_image)
            }
            resourceVo.contenttype.equals("activity", true) -> {
                holder.imgPlay.visibility = View.GONE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_doc)
            }
            resourceVo.contenttype.equals("audio", true) -> {
                holder.imgPlay.visibility = View.VISIBLE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_audio)
            }
            else ->{

            }
        }
        holder.tvResourceGenerator.text = resourceVo.ResourceOriginator
        if (resourceVo.ResourceOriginator.equals("shared", true)) {
            holder.tvResourceGenerator.background =
                holder.cardMain.context.resources.getDrawable(R.drawable.ic_label_orange)
        } else {
            holder.tvResourceGenerator.background =
                holder.cardMain.context.resources.getDrawable(R.drawable.ic_label_my)
        }
    }
}

class TopicResourceHolder : KotlinEpoxyHolder() {
    val cardMain by bind<com.google.android.material.card.MaterialCardView>(R.id.card_video)
    val tvTitle by bind<TextView>(R.id.tv_title)
    val tvSubTitle by bind<TextView>(R.id.tv_sub_title)
    val iconResourceType by bind<View>(R.id.vw_resource_type)
    val tvResourceGenerator by bind<TextView>(R.id.tv_resource_generator)
    val ivBackground by bind<AppCompatImageView>(R.id.img_video_thumb)
    val imgPlay by bind<AppCompatImageView>(R.id.img_play)
}
