package com.tce.teacherapp.ui.dashboard.home.adapter

import android.text.Html
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.dashboard.subjects.loadImage
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.CustomCircularImageView

@EpoxyModelClass(layout = R.layout.dashboard_header)
abstract class HeaderEpoxyHolder : EpoxyModelWithHolder<HeaderHolder>() {

    @EpoxyAttribute
    lateinit var strName : String

    @EpoxyAttribute
    lateinit var strLastSync : String

    @EpoxyAttribute
    lateinit var imageUrl: String

    override fun bind(holder: HeaderHolder) {

        holder.tvUserName.text = strName
        val sourceString = "<i>last sync with In-Class App on <b>$strLastSync</b></i>"
        holder.tvLastSync.text = Html.fromHtml(sourceString)
        holder.glide.loadImage(imageUrl).into(holder.imageView)
    }
}

class HeaderHolder : KotlinEpoxyHolder(){
    val tvUserName by bind<TextView>(R.id.tvName)
    val tvLastSync by bind<TextView>(R.id.tv_last_sync)
    val imageView by bind<CustomCircularImageView>(R.id.img_profile)
    val glide by lazy { Glide.with(imageView.context) }
}