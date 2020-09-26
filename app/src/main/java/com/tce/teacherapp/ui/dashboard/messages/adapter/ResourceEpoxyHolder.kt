package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_resource)
abstract class ResourceEpoxyHolder  : EpoxyModelWithHolder<ResourceHolder>(){

    @EpoxyAttribute
    lateinit var strTitle : String
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: ResourceHolder) {
        holder.tvTitle.text = strTitle
        holder.tvAdd.setOnClickListener {
            listener()
        }
    }
}

class ResourceHolder : KotlinEpoxyHolder(){
    val tvTitle by bind<TextView>(R.id.title)
    val tvAdd by bind<TextView>(R.id.tvAdd)

}