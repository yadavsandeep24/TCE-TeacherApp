package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.card.MaterialCardView
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.list_item_selected_student)
abstract class SelectedStudentEpoxyHolder : EpoxyModelWithHolder<StudentHolder>(){

    @EpoxyAttribute
    lateinit var strName : String
    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: StudentHolder) {

        holder.tvStudentName.text = strName
        holder.imgClose.setOnClickListener { listener() }
    }
}

class StudentHolder : KotlinEpoxyHolder(){
    val tvStudentName by bind<TextView>(R.id.tv_student_name)
    val lnrMainContainer by bind<MaterialCardView>(R.id.card_student)
    val imgClose by bind<AppCompatImageView>(R.id.img_close)
}