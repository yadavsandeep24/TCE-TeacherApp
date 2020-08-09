package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Feedback
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.db.entity.Student
import java.util.*


class StudentPortfolioAdapter(val context: Context, val isShowCheckBox : Boolean) :
    RecyclerView.Adapter<StudentPortfolioAdapter.StudentPortfolioViewHolder>() {


    var modelList: MutableList<Portfolio> = ArrayList<Portfolio>()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: StudentPortfolioViewHolder, position: Int) {
        holder?.tvDate?.setText(modelList[position].Date)
        if (position == 0) {
            holder.dotIndicator.background =
                context.resources.getDrawable(R.drawable.ic_portfolio_blue_dot)
        } else {
            holder.dotIndicator.background =
                context.resources.getDrawable(R.drawable.ic_portfolio_dark_dot)
        }

        if(modelList[position].Gallery!= null  && modelList[position].Gallery.size > 0){
            holder.cardGallary.visibility = View.VISIBLE
            holder.rvGallary.layoutManager = GridLayoutManager(context, 2)
            holder.rvGallary.setHasFixedSize(true)
            var myAdapter = StudentPortfolioGallaryAdapter(context, isShowCheckBox)
            holder.rvGallary.adapter = myAdapter
            myAdapter?.modelList = modelList[position].Gallery as MutableList<StudentGalleryData>
            myAdapter?.notifyDataSetChanged()

        }else{
            holder.cardGallary.visibility = View.GONE
        }

        if(modelList[position].Feedback!= null  && modelList[position].Feedback.size > 0){
            holder.rvFeedback.visibility = View.VISIBLE
            holder.rvFeedback.layoutManager = GridLayoutManager(context, 1)
            holder.rvFeedback.setHasFixedSize(true)
            var myAdapter = StudentPortfolioFeedbackAdapter(context)
            holder.rvFeedback.adapter = myAdapter
            myAdapter?.modelList = modelList[position].Feedback as MutableList<Feedback>
            myAdapter?.notifyDataSetChanged()

        }else{
            holder.rvFeedback.visibility = View.GONE
        }

        if(!TextUtils.isEmpty(modelList[position].TeacherNote) ) {
            holder.cardTeacherNote.visibility = View.VISIBLE
            holder.tvTeacherNote.setText(modelList[position].TeacherNote)
        }else{
            holder.cardTeacherNote.visibility = View.GONE
        }

        if(isShowCheckBox){
            holder.chkFeedback.visibility = View.VISIBLE
            holder.chkGallary.visibility = View.VISIBLE
            holder.chkTeacherNote.visibility = View.VISIBLE
        }else{
            holder.chkFeedback.visibility = View.GONE
            holder.chkGallary.visibility = View.GONE
            holder.chkTeacherNote.visibility = View.GONE
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentPortfolioViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_portfolio, parent, false)
        return StudentPortfolioViewHolder(itemView)
    }

    class StudentPortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvDate: TextView
        val dotIndicator: AppCompatImageView
        val cardGallary : CardView
        val rvGallary : RecyclerView
        val viewAllContainer : LinearLayout
        val rvFeedback : RecyclerView
        val cardTeacherNote : CardView
        val tvTeacherNote: TextView
        val chkGallary: CheckBox
        val chkFeedback: CheckBox
        val chkTeacherNote :CheckBox


        init {
            tvDate = itemView.findViewById(R.id.tvDate)
            dotIndicator = itemView.findViewById(R.id.dot_indicator)
            cardGallary = itemView.findViewById(R.id.card_gallary)
            rvGallary = itemView.findViewById(R.id.rv_gallary)
            viewAllContainer = itemView.findViewById(R.id.view_all_container)
            rvFeedback = itemView.findViewById(R.id.rv_feddback)
            cardTeacherNote = itemView.findViewById(R.id.card_teacher_note)
            tvTeacherNote = itemView.findViewById(R.id.tv_teacher_note)
            chkGallary = itemView.findViewById(R.id.chk_gallary)
            chkFeedback = itemView.findViewById(R.id.chk_feedback)
            chkTeacherNote = itemView.findViewById(R.id.chk_teacher_note)
        }


    }


}