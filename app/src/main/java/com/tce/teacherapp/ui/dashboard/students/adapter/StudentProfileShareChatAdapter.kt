package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Feedback
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentGalleryData
import java.util.*


class StudentProfileShareChatAdapter(val context: Context) :
    RecyclerView.Adapter<StudentProfileShareChatAdapter.StudentPortfolioViewHolder>() {


    var modelList: MutableList<Portfolio> = ArrayList()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: StudentPortfolioViewHolder, position: Int) {

        if(modelList[position].Gallery!= null  && modelList[position].Gallery.isNotEmpty()){
            holder.rvGallary.visibility = View.VISIBLE
            holder.rvGallary.layoutManager = GridLayoutManager(context, 1)
            holder.rvGallary.setHasFixedSize(true)
            val myAdapter = StudentProfileShareChatGallaryAdapter(context)
            holder.rvGallary.adapter = myAdapter
            myAdapter.modelList = modelList[position].Gallery as MutableList<StudentGalleryData>
            myAdapter.notifyDataSetChanged()
        }else{
            holder.rvGallary.visibility = View.GONE
        }

        if(modelList[position].Feedback!= null  && modelList[position].Feedback.isNotEmpty()){
            holder.rvFeedback.visibility = View.VISIBLE
            holder.rvFeedback.layoutManager = GridLayoutManager(context, 1)
            holder.rvFeedback.setHasFixedSize(true)
            val myAdapter = StudentProfileShareChatFeedbackAdapter(context)
            holder.rvFeedback.adapter = myAdapter
            myAdapter.modelList = modelList[position].Feedback as MutableList<Feedback>
            myAdapter.notifyDataSetChanged()
        }else{
            holder.rvFeedback.visibility = View.GONE
        }

        if(!TextUtils.isEmpty(modelList[position].TeacherNote) ) {
            holder.tvTeacherNote.visibility = View.VISIBLE
            holder.tvTeacherNote.text = modelList[position].TeacherNote
        }else {
            holder.tvTeacherNote.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentPortfolioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_portfolio_share_chat, parent, false)
        return StudentPortfolioViewHolder(itemView)
    }

    class StudentPortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rvGallary : RecyclerView = itemView.findViewById(R.id.rv_gallery)
        val rvFeedback : RecyclerView = itemView.findViewById(R.id.rv_feedback)
        val tvTeacherNote: TextView = itemView.findViewById(R.id.tv_teacher_note)
    }


}