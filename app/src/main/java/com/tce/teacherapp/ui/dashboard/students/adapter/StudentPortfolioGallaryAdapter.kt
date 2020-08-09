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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.db.entity.Student
import java.util.*


class StudentPortfolioGallaryAdapter(val context: Context, val isShowCheckBox: Boolean) :
    RecyclerView.Adapter<StudentPortfolioGallaryAdapter.GalllaryAdapter>() {


    var modelList: MutableList<StudentGalleryData> = ArrayList<StudentGalleryData>()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: GalllaryAdapter, position: Int) {

        Glide.with(context)
            .load(modelList[position].src)
            .placeholder(R.drawable.dummy_video)
            .into(holder.imgGallary)

        if(modelList[position].contenttype.equals("AV", ignoreCase = true)){
            holder.imgPlay.visibility = View.VISIBLE
        }else{
            holder.imgPlay.visibility = View.GONE
        }
        if(isShowCheckBox){
            holder.chkGallary.visibility = View.VISIBLE
        }else{
            holder.chkGallary.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalllaryAdapter {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_portfolio_gallary, parent, false)
        return GalllaryAdapter(itemView)
    }

    class GalllaryAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgGallary: AppCompatImageView
        val imgPlay: AppCompatImageView
        val chkGallary: CheckBox
        init {
            imgGallary = itemView.findViewById(R.id.img_gallary)
            imgPlay = itemView.findViewById(R.id.img_play)
            chkGallary = itemView.findViewById(R.id.chk_gallary)

        }


    }


}