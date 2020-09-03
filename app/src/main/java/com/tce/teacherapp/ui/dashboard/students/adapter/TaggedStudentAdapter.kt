package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
@FlowPreview
@ExperimentalCoroutinesApi
class TaggedStudentAdapter(val context: Context, val listener: ViewHolderClickListener) : RecyclerView.Adapter<TaggedStudentViewHolder>(){
    private var isShowCancelIcon: Boolean = false

    fun setIsShowCancelIcon(isShow: Boolean) {
        isShowCancelIcon = isShow
        notifyDataSetChanged()
    }

    var modelList: MutableList<StudentListResponseItem> = ArrayList()

    override fun getItemCount() = modelList.size

    override fun onBindViewHolder(holder: TaggedStudentViewHolder, position: Int) {
        holder.tvStudentName.text = modelList[position].Name
        if(isShowCancelIcon){
            holder.vwCancel.visibility = View.VISIBLE
        }else{
            holder.vwCancel.visibility = View.GONE
        }
        holder.vwCancel.setOnClickListener {
            listener.onTap(position,modelList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaggedStudentViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_tagged_student, parent, false)
        return TaggedStudentViewHolder(itemView)
    }


}