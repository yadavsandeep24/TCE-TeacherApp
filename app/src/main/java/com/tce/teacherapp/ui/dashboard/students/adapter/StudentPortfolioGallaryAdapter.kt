package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import java.util.*


class StudentPortfolioGallaryAdapter(val context: Context, val isShowCheckBox: Boolean,val listener: IStudentGalleryClickListener) :
    RecyclerView.Adapter<StudentPortfolioGallaryAdapter.GalllaryAdapter>() {


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
        if(isShowCheckBox) {
            holder.chkGallary.visibility = View.VISIBLE
            holder.chkGallary.isChecked = modelList[position].isSelected
            if (modelList[position].isSelected) {
                holder.cardMain.strokeWidth =
                    context.resources.getDimension(R.dimen.card_stroke_width_3dp).toInt()
            } else {
                holder.cardMain.strokeWidth =
                    context.resources.getDimension(R.dimen.card_stroke_width_0dp).toInt()
            }
            holder.chkGallary.setOnCheckedChangeListener { _, isChecked ->
                modelList[position].isSelected = isChecked
                listener.onCheckBoxClicked(modelList[position])
                notifyDataSetChanged()
            }
        }else{
            holder.chkGallary.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(modelList[position])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalllaryAdapter {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_portfolio_gallary, parent, false)
        return GalllaryAdapter(itemView)
    }

    class GalllaryAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardMain: MaterialCardView = itemView.findViewById(R.id.cv_main)
        val imgGallary: AppCompatImageView = itemView.findViewById(R.id.img_gallary)
        val imgPlay: AppCompatImageView = itemView.findViewById(R.id.img_play)
        val chkGallary: CheckBox = itemView.findViewById(R.id.chk_gallary)


    }


}