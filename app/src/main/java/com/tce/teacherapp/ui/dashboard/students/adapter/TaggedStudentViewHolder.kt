package com.tce.teacherapp.ui.dashboard.students.adapter

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
class TaggedStudentViewHolder(itemView: View, val r_tap: ViewHolderClickListener) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener, View.OnClickListener {

    val tvStudentName: TextView = itemView.findViewById(R.id.tv_student_name)
    val vwCancel: View = itemView.findViewById(R.id.vw_cancel)
    val imgStudent = itemView.findViewById<AppCompatImageView>(R.id.img_student)

    init {
        vwCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        r_tap.onTap(adapterPosition,null)
    }

    override fun onLongClick(v: View?): Boolean {
        r_tap.onLongTap(adapterPosition)
        return true
    }
}