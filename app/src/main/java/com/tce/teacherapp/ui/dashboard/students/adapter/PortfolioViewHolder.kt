package com.tce.teacherapp.ui.dashboard.students.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.edgedevstudio.example.recyclerviewmultiselect.ViewHolderClickListener
import com.tce.teacherapp.R

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
class PortfolioViewHolder(itemView: View, val r_tap: ViewHolderClickListener) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener, View.OnClickListener {

    val tvStudentName: TextView
    val mainContainer: LinearLayout
    val imgStudent : AppCompatImageView
    val chkSelect : CheckBox

    init {
        tvStudentName = itemView.findViewById(R.id.tv_student_name)
        mainContainer = itemView.findViewById(R.id.root_layout)
        imgStudent = itemView.findViewById(R.id.img_student)
        chkSelect = itemView.findViewById(R.id.chk_completed)
        mainContainer.setOnClickListener(this)
        mainContainer.setOnLongClickListener(this)
    }

    override fun onClick(v: View?) {
        r_tap.onTap(adapterPosition)
    }

    override fun onLongClick(v: View?): Boolean {
        r_tap.onLongTap(adapterPosition)
        return true
    }
}