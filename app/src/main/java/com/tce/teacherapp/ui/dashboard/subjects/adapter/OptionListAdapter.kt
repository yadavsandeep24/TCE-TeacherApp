package com.tce.teacherapp.ui.dashboard.subjects.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.OptionListData
import com.tce.teacherapp.db.entity.Subject
import com.tce.teacherapp.db.entity.Topic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*


@FlowPreview
@ExperimentalCoroutinesApi
class OptionListAdapter(val context: Context) :
    RecyclerView.Adapter<OptionListAdapter.OptionListViewHolder>() {


    var modelList: List<Any> = ArrayList()

    fun setData(itemList  :List<Any>){
        modelList = itemList
        notifyDataSetChanged()
    }

    override fun getItemCount() = modelList.size

    override fun onBindViewHolder(holder: OptionListViewHolder, position: Int) {
        val optionData = modelList[position]
        when (optionData) {
            is Grade -> {
                holder.tvOptionName.text = optionData.gradeTitle
            }
            is Subject ->{
                holder.tvOptionName.text = optionData.title
            }
            is Topic ->{
                holder.tvOptionName.text = optionData.label
            }
            is OptionListData ->{
                holder.tvOptionName.text = optionData.name
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_option_list_item, parent, false)
        return OptionListViewHolder(itemView)
    }

    class OptionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvOptionName: TextView = itemView.findViewById(R.id.tv_option_name)
        val chkSelect: CheckBox = itemView.findViewById(R.id.chk_select)
    }

}
