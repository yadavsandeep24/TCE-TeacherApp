package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.*
import com.tce.teacherapp.db.entity.Student
import org.w3c.dom.Text
import java.util.*


class ProgressCardAdapter(val context: Context) :
    RecyclerView.Adapter<ProgressCardAdapter.ProgressAdapter>() {


    var modelList: MutableList<ProgressData> = ArrayList<ProgressData>()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: ProgressAdapter, position: Int) {
        holder.tvName.setText(modelList[position].Name)

        holder.cardContainer.setOnClickListener(View.OnClickListener {
            if (holder.rvObjective.visibility == View.VISIBLE) {
                holder.imgArraow.background = context.resources.getDrawable(R.drawable.arrow_down)
                holder.rvObjective.visibility = View.GONE
            } else {
                holder.imgArraow.background = context.resources.getDrawable(R.drawable.arrow_up)
                holder.rvObjective.visibility = View.VISIBLE

                holder.rvObjective.layoutManager = GridLayoutManager(context, 1)
                holder.rvObjective.setHasFixedSize(true)
                var myAdapter = ProgressObjectiveAdapter(context)
                holder.rvObjective.adapter = myAdapter
                myAdapter?.modelList = modelList[position].Objectives as MutableList<Objective>
                myAdapter?.notifyDataSetChanged()

            }
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressAdapter {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_progress_card, parent, false)
        return ProgressAdapter(itemView)
    }

    class ProgressAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView
        val imgArraow: AppCompatImageView
        val rvObjective: RecyclerView
        val cardContainer: RelativeLayout

        init {
            tvName = itemView.findViewById(R.id.tvName)
            imgArraow = itemView.findViewById(R.id.img_arrow)
            rvObjective = itemView.findViewById(R.id.rv_objective)
            cardContainer = itemView.findViewById(R.id.card_container)
        }


    }


}