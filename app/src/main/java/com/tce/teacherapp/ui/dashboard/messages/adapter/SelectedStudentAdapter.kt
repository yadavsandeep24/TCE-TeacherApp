package com.tce.teacherapp.ui.dashboard.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*


@FlowPreview
@ExperimentalCoroutinesApi
class SelectedStudentAdapter(val context: Context, val listener: ISelectedStudentClickListener) :
    RecyclerView.Adapter<SelectedStudentAdapter.SelectStudentViewHolder>() {


    var modelList: MutableList<StudentListResponseItem> = ArrayList()

    override fun getItemCount() = modelList.size

    override fun onBindViewHolder(holder: SelectStudentViewHolder, position: Int) {
        holder.tvStudentName.text = modelList[position].Name

        /*      if (modelList[position].isSelected) {
                  holder.lnrMainContainer.setBackgroundColor(holder.lnrMainContainer.resources.getColor(bgSelectionColor))
              }else{
                  holder.lnrMainContainer.setBackgroundColor(holder.lnrMainContainer.resources.getColor(R.color.transparent))
              }*/
        Glide.with(context)
            .load(modelList[position].ImagePath)
            .placeholder(R.drawable.ic_profile_icon)
            .into(holder.ivStudent)
        holder.ivClose.setOnClickListener {
            val selectedtItem = modelList[position]
            modelList.remove(selectedtItem)
            notifyDataSetChanged()
            listener.onClose(selectedtItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectStudentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_selected_student, parent, false)
        return SelectStudentViewHolder(itemView)
    }

    class SelectStudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvStudentName: TextView = itemView.findViewById(R.id.tv_student_name)
        val ivStudent: AppCompatImageView = itemView.findViewById(R.id.img_user)
        val ivClose: AppCompatImageView = itemView.findViewById(R.id.img_close)
    }

}


interface ISelectedStudentClickListener {
    fun onClose(item: StudentListResponseItem)
}