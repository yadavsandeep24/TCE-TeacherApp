package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_resource_type)
abstract class ResourceTypeEpoxyHolder  : EpoxyModelWithHolder<ResourceTypeHolder>(){

    @EpoxyAttribute
    lateinit var strType : String
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    @JvmField
    @EpoxyAttribute
    var isSelected:  Boolean  =  false

    override fun bind(holder: ResourceTypeHolder) {
        holder.tvType.setText(strType)

        holder.tvType.background = null
        holder.tvType.setOnClickListener {
            if(isSelected) {
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