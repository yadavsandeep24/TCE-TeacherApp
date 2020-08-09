package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Feedback
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.db.entity.Student
import org.w3c.dom.Text
import java.util.*


class StudentPortfolioFeedbackAdapter(val context: Context) :
    RecyclerView.Adapter<StudentPortfolioFeedbackAdapter.FeedbackAdapter>() {


    var modelList: MutableList<Feedback> = ArrayList<Feedback>()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: FeedbackAdapter, position: Int) {
       holder.tvFeedback.setText(modelList[position].Name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackAdapter {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_portfolio_feedback, parent, false)
        return FeedbackAdapter(itemView)
    }

    class FeedbackAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvFeedback: TextView

        init {
            tvFeedback = itemView.findViewById(R.id.tv_feedback)

        }


    }


}