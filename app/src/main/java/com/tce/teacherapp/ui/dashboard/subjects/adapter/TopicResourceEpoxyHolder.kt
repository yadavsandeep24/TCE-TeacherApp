package com.tce.teacherapp.ui.dashboard.subjects.adapter

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.list_item_topic_resource)
abstract class TopicResourceEpoxyHolder : EpoxyModelWithHolder<TopicResourceHolder>() {

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: TopicResourceHolder) {
        holder.cardMain.setOnClickListener { listener() }
    }
}

class TopicResourceHolder : KotlinEpoxyHolder() {
    val cardMain by bind<com.google.android.material.card.MaterialCardView>(R.id.card_video)
}
