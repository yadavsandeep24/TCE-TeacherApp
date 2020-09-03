package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Objective
import java.util.*


class ProgressObjectiveAdapter(val context: Context) :
    RecyclerView.Adapter<ProgressObjectiveAdapter.ObjectiveAdapter>() {


    var modelList: MutableList<Objective> = ArrayList<Objective>()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: ObjectiveAdapter, position: Int) {
       holder.tvObjective.setText(modelList[position].Name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectiveAdapter {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_progress_objective, parent, false)
        return ObjectiveAdapter(itemView)
    }

    class ObjectiveAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvObjective: TextView = itemView.findViewById(R.id.tvObjective)


    }


}