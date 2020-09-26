package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*


@FlowPreview
@ExperimentalCoroutinesApi
class NewMessageAdapter(val context: Context, val bgSelectionColor:Int,val listener :ViewHolderClickListener) : RecyclerView.Adapter<NewMessageAdapter.NewMessageViewHolder>() {


    private var modelList: List<StudentListResponseItem> = ArrayList()

     fun setData(itemList  :List<StudentListResponseItem>){
        modelList = itemList
        notifyDataSetChanged()
    }

    fun updateListItem(selectedItem: StudentListResponseItem) {
        for (item in modelList){
            if(item.id == selectedItem.id){
                item.isSelected = false
                break
            }
        }
        notifyDataSetChanged()

    }
    override fun getItemCount() = modelList.size

    override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int) {
        holder.tvStudentName.text = modelList[position].Name

        val id = modelList[position].id
        Glide.with(context)
            .load(modelList[position].ImagePath)
            .placeholder(R.drawable.ic_profile_icon)
            .into(holder.ivStudent)
        if (modelList[position].isSelected) {
            holder.lnrMainContainer.setBackgroundColor(holder.lnrMainContainer.resources.getColor(bgSelectionColor))
        }else{
            holder.lnrMainContainer.setBackgroundColor(holder.lnrMainContainer.resources.getColor(R.color.transparent))
        }
        holder.lnrMainContainer.setOnClickListener {
            modelList[position].isSelected = true
            notifyDataSetChanged()
            listener.onTap(position,modelList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_new_message, parent, false)
        return NewMessageViewHolder(itemView)
    }
    class NewMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvStudentName : TextView =  itemView.findViewById(R.id.tv_student_name)
        val lnrMainContainer: LinearLayout =  itemView.findViewById(R.id.student_container)
        val ivStudent : AppCompatImageView = itemView.findViewById(R.id.img_user)
    }

}

