package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder


@EpoxyModelClass(layout = R.layout.list_item_new_message)
abstract class NewMessageEpoxyHolder: EpoxyModelWithHolder<Holder2>() {

    @EpoxyAttribute
    lateinit var strStudentName : String
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: Holder2) {

        holder.tvStudentName.text = strStudentName
        holder.lnrMainContainer.setOnClickListener { listener() }
    }
}

class Holder2 : KotlinEpoxyHolder(){

    val tvStudentName by bind<TextView>(R.id.tv_student_name)
    val lnrMainContainer by bind<LinearLayout>(R.id.student_container)

}