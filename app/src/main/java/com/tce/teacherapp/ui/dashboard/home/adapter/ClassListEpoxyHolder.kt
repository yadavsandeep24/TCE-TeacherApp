package com.tce.teacherapp.ui.dashboard.home.adapter

import android.graphics.drawable.Drawable
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.Utility

@EpoxyModelClass(layout = R.layout.list_item_dashboard_filter)
abstract class ClassListEpoxyHolder : EpoxyModelWithHolder<ClassHolder>(){


    @EpoxyAttribute
    lateinit var imageUrl : String

    @EpoxyAttribute
    lateinit var name : String

    @EpoxyAttribute
    lateinit var shortName : String

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    @EpoxyAttribute
    lateinit var imageDrawable : Drawable

    override fun bind(holder: ClassHolder) {
        super.bind(holder)

        Utility.setSelectorRoundedCorner(
            holder.mainContainer!!.context,  holder.mainContainer!!, 0,
            R.color.transparent, R.color.dim_color,
            R.color.transparent, R.color.transparent, 0
        )


        holder.mainContainer.setOnClickListener{listener()}
        holder.tvIcon.setText(shortName)
        holder.tvIcon.background = imageDrawable
        holder.tvTitle.setText(name)

    }
}

class ClassHolder : KotlinEpoxyHolder(){
    val tvIcon by bind<TextView>(R.id.tv_icon)
    val tvTitle by bind<TextView>(R.id.tvTitle)
    val mainContainer by bind<LinearLayout>(R.id.main_container)

}