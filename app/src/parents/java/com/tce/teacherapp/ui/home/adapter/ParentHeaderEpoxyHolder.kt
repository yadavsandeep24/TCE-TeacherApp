package com.tce.teacherapp.ui.home.adapter

import android.text.Html
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.dashboard_header)
abstract class ParentHeaderEpoxyHolder : EpoxyModelWithHolder<HeaderHolder>() {

    @EpoxyAttribute
    lateinit var strName : String

    @EpoxyAttribute
    lateinit var strLastSync : String

    override fun bind(holder: HeaderHolder) {

        holder.tvUserName.text = strName
        val sourceString = "<i>last sync with In-Class App on <b>$strLastSync</b></i>"
        holder.tvLastSync.text = Html.fromHtml(sourceString)
    }
}

class HeaderHolder : KotlinEpoxyHolder(){
    val tvUserName by bind<TextView>(R.id.tvName)
    val tvLastSync by bind<TextView>(R.id.tv_last_sync)
}