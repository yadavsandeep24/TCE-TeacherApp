package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Objective
import com.tce.teacherapp.api.response.ProgressData
import java.util.*


class ProgressCardAdapter(val context: Context) :
    RecyclerView.Adapter<ProgressCardAdapter.ProgressAdapter>() {


    var modelList: List<ProgressData> = ArrayList()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: ProgressAdapter, position: Int) {
        holder.tvName.text = modelList[position].Name
        if(position ==0){
            holder.imgArraow.background = context.resources.getDrawable(R.drawable.arrow_up)
            holder.rvObjective.visibility = View.VISIBLE
            holder.rvObjective.layoutManager = GridLayoutManager(context, 1)
            holder.rvObjective.setHasFixedSize(true)
            val myAdapter = ProgressObjectiveAdapter(context)
            holder.rvObjective.adapter = myAdapter
            myAdapter.modelList = modelList[0].Objectives as MutableList<Objective>
            myAdapter.notifyDataSetChanged()
        }
        holder.cardContainer.setOnClickListener {
            if (holder.rvObjective.visibility == View.VISIBLE) {
                holder.imgArraow.background = context.resources.getDrawable(R.drawable.arrow_down)
                holder.rvObjective.visibility = View.GONE
            } else {
                holder.imgArraow.background = context.resources.getDrawable(R.drawable.arrow_up)
                holder.rvObjective.visibility = View.VISIBLE

                holder.rvObjective.layoutManager = GridLayoutManager(context, 1)
                holder.rvObjective.setHasFixedSize(true)
                val myAdapter = ProgressObjectiveAdapter(context)
                holder.rvObjective.adapter = myAdapter
                myAdapter.modelList = modelList[position].Objectives as MutableList<Objective>
                myAdapter.notifyDataSetChanged()

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressAdapter {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_progress_card, parent, false)
        return ProgressAdapter(itemView)
    }

    class ProgressAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val imgArraow: AppCompatImageView = itemView.findViewById(R.id.img_arrow)
        val rvObjective: RecyclerView = itemView.findViewById(R.id.rv_objective)
        val cardContainer: RelativeLayout = itemView.findViewById(R.id.card_container)


    }


}