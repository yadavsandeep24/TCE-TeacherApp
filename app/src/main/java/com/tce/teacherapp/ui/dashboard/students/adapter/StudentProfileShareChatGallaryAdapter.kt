package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import java.util.*


class StudentProfileShareChatGallaryAdapter(val context: Context) :
    RecyclerView.Adapter<StudentProfileShareChatGallaryAdapter.GalllaryAdapter>() {


    var modelList: MutableList<StudentGalleryData> = ArrayList()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: GalllaryAdapter, position: Int) {

        Glide.with(context)
            .load(modelList[position].image)
            .placeholder(R.drawable.dummy_video)
            .into(holder.imgGallary)

        if(modelList[position].contenttype.equals("AV", ignoreCase = true)){
            holder.imgPlay.visibility = View.VISIBLE
        }else{
            holder.imgPlay.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalllaryAdapter {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_portfolio_share_chat_gallary, parent, false)
        return GalllaryAdapter(itemView)
    }

    class GalllaryAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgGallary: AppCompatImageView = itemView.findViewById(R.id.img_gallary)
        val imgPlay: AppCompatImageView = itemView.findViewById(R.id.img_play)


    }


}