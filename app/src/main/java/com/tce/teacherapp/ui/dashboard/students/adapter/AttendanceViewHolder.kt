package com.tce.teacherapp.ui.dashboard.students.adapter

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R


class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvStudentName: TextView = itemView.findViewById(R.id.tv_student_name)
    val mainContainer: LinearLayout = itemView.findViewById(R.id.root_layout)
    val imgStudent : AppCompatImageView = itemView.findViewById(R.id.img_student)
    val flIndicator : FrameLayout =  itemView.findViewById(R.id.fl_indicator)

    /*init {
        mainContainer.setOnClickListener(this)
        mainContainer.setOnLongClickListener(this)
    }*/

/*    override fun onClick(v: View?) {
        r_tap.onTap(adapterPosition,null)
    }

    override fun onLongClick(v: View?): Boolean {
        r_tap.onLongTap(adapterPosition)
        return true
    }*/
}