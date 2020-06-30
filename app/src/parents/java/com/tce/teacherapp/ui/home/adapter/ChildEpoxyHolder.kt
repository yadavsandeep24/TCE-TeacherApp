package com.tce.teacherapp.ui.home.adapter

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_child)
abstract  class ChildEpoxyHolder : EpoxyModelWithHolder<InfoHolder>(){

    @EpoxyAttribute
    lateinit var strStudentName : String
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: InfoHolder) {

        holder.tvStudentName.text = strStudentName
        holder.lnrMainContainer.setOnClickListener { listener() }
        if(strStudentName.contains("Amit")){
            holder.iconTick.visibility = View.VISIBLE
            holder.tvStudentName.setTextColor(Color.BLUE)
        }else
        {
            holder.tvStudentName.setTextColor(Color.BLACK)
            holder.iconTick.visibility = View.GONE
        }
    }
}

class InfoHolder : KotlinEpoxyHolder(){

    val iconTick by bind<AppCompatImageView>(R.id.icon_tick)
    val tvStudentName by bind<TextView>(R.id.tv_student_name)
    val lnrMainContainer by bind<LinearLayout>(R.id.student_container)

}