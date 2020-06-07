package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_resource_type)
abstract class ResourceTypeEpoxyHolder  : EpoxyModelWithHolder<ResourceTypeHolder>(){

    @EpoxyAttribute
    lateinit var strType : String
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: ResourceTypeHolder) {
        holder.tvType.setText(strType)

        holder.tvType.background = null
        holder.tvType.setOnClickListener {
            if(holder.tvType.background == null) {
                holder.tvType.background =
                    holder.tvType.context.resources.getDrawable(R.drawable.bg_yellow_white_rounded)
            }
            listener()
        }
    }
}

class ResourceTypeHolder : KotlinEpoxyHolder(){
    val tvType by bind<TextView>(R.id.tvresource)
}