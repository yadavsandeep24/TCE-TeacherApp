package com.tce.teacherapp.ui.dashboard.students.adapter

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
class TaggedStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val tvStudentName: TextView = itemView.findViewById(R.id.tv_student_name)
    val vwCancel: View = itemView.findViewById(R.id.vw_cancel)
    val imgStudent = itemView.findViewById<AppCompatImageView>(R.id.img_student)
}