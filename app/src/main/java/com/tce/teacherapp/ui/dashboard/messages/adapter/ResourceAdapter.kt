package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
class ResourceAdapter(val context: Context, val listener: IResourceItemSelectedListener) :
    RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder>() {


    var modelList: List<Resource> = emptyList()
    private var selectedPosition = -1

    fun setData(list: List<Resource>) {
        modelList = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = modelList.size

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resourceVo = modelList[position]
        holder.tvTitle.text = modelList[position].title
        holder.itemView.setOnClickListener {
            selectedPosition = position
            listener.onResourceSelected(modelList[position])
        }
        Glide.with(holder.cardMain.context)
            .load(resourceVo.image)
            .placeholder(R.drawable.topic_resource_bg)
            .into(holder.ivBackground)
        when {
            resourceVo.contenttype.equals("av", true) -> {
                holder.imgPlay.visibility = View.VISIBLE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_video)
            }
            resourceVo.contenttype.equals("Image", true) -> {
                holder.imgPlay.visibility = View.GONE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_image)
            }
            resourceVo.contenttype.equals("activity", true) -> {
                holder.imgPlay.visibility = View.GONE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_doc)
            }
            resourceVo.contenttype.equals("audio", true) -> {
                holder.imgPlay.visibility = View.VISIBLE
                holder.iconResourceType.background =
                    holder.cardMain.context.resources.getDrawable(R.drawable.ic_media_type_audio)
            }
            else ->{

            }
        }
        holder.tvResourceGenerator.text = resourceVo.ResourceOriginator
        when {
            resourceVo.ResourceOriginator.equals("shared", true) -> {
                holder.tvResourceGenerator.background =
                    holder.tvResourceGenerator.context.resources.getDrawable(R.drawable.ic_label_orange)
            }
            resourceVo.ResourceOriginator.equals("tce", true) -> {
                holder.tvResourceGenerator.background =
                    holder.tvResourceGenerator.context.resources.getDrawable(R.drawable.ic_label_tce)
            }
            else -> {
                holder.tvResourceGenerator.background =
                    holder.tvResourceGenerator.context.resources.getDrawable(R.drawable.ic_label_my)
            }
        }

        if(resourceVo.isAdded){
            holder.tvAdded.background =
                holder.tvAdded.context.resources.getDrawable(R.drawable.ic_added_resource)
        }else{
            holder.tvAdded.background =
                holder.tvAdded.context.resources.getDrawable(R.drawable.ic_add_resource)
        }

        holder.tvAdded.setOnClickListener {
            resourceVo.isAdded = !resourceVo.isAdded
            notifyDataSetChanged()
            listener.onAddedClicked(resourceVo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_resource, parent, false)
        return ResourceViewHolder(itemView)
    }

    class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvAdded: TextView = itemView.findViewById(R.id.tvAdd)
        val tvResourceGenerator: TextView = itemView.findViewById(R.id.tv_resource_generator)
        val imgPlay: AppCompatImageView = itemView.findViewById(R.id.img_play)
        val cardMain: MaterialCardView = itemView.findViewById(R.id.card_video)
        val iconResourceType: View = itemView.findViewById(R.id.vw_resource_type)
        val ivBackground: AppCompatImageView = itemView.findViewById(R.id.img_video_thumb)
    }

}

interface IResourceItemSelectedListener {
    fun onResourceSelected(item: Resource)
    fun onAddedClicked(item: Resource)
}
