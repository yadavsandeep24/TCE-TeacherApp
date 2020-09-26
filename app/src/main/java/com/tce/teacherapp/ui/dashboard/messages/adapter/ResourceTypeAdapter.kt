package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
class ResourceTypeAdapter(val context: Context, val listener: IResourceTypeItemSelectedListener) :
    RecyclerView.Adapter<ResourceTypeAdapter.ResourceTypeViewHolder>() {


    var modelList: List<Resource> = emptyList()
    private var selectedPosition =-1

    fun setResourceTypeSelectedPosition(pos: Int) {
        selectedPosition = pos
        notifyDataSetChanged()
    }

    fun setData(list: List<Resource>,position: Int){
        modelList =list
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getItemCount() = modelList.size

    override fun onBindViewHolder(holder: ResourceTypeViewHolder, position: Int) {
        holder.tvResourceType.text = modelList[position].ResourceOriginator
        if(selectedPosition == -1){
            holder.tvResourceType.background =
                holder.tvResourceType.context.resources.getDrawable(R.drawable.transparent)
        }else{
            if(selectedPosition == position) {
                holder.tvResourceType.background =
                    holder.tvResourceType.context.resources.getDrawable(R.drawable.bg_yellow_white_rounded)
            }else{
                holder.tvResourceType.background =
                    holder.tvResourceType.context.resources.getDrawable(R.drawable.transparent)
            }
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            listener.onResourceTypeSelected(modelList[position],selectedPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceTypeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_resource_type, parent, false)

        return ResourceTypeViewHolder(itemView)
    }

    class ResourceTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvResourceType: TextView = itemView.findViewById(R.id.tvresource)
    }

}


interface IResourceTypeItemSelectedListener {
    fun onResourceTypeSelected(item: Resource,position:Int)
}