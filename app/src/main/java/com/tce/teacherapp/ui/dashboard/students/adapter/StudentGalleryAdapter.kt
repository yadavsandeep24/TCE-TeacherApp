package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.api.response.StudentGalleryResponseItem
import java.text.SimpleDateFormat
import java.util.*


class StudentGalleryAdapter(
    val context: Context,
    val listener: IStudentGalleryClickListener,
    val isShowCheckBox: Boolean
) :
    RecyclerView.Adapter<StudentGalleryAdapter.StudentPortfolioViewHolder>(),IStudentGalleryClickListener {


    var modelList: MutableList<StudentGalleryResponseItem> = ArrayList()
    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: StudentPortfolioViewHolder, position: Int) {

        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)

        val date = simpleDateFormat.parse(modelList[position].Date)

        val formattedString: String =  SimpleDateFormat("dd MMMM yyyy EEEE").format(date)
        holder.tvDate.text = formattedString
        if(isShowCheckBox){
            holder.chkDate.visibility = View.VISIBLE
            holder.chkDate.setOnCheckedChangeListener { _, isChecked ->
                for (item in modelList[position].studentGalleryDataList){
                    item.isSelected = isChecked
                }
                listener.onDateCheckBoxClicked(modelList[position])
                notifyDataSetChanged()
            }
        }else{
            holder.chkDate.visibility = View.GONE
        }


        if(modelList[position].studentGalleryDataList!= null  && modelList[position].studentGalleryDataList.isNotEmpty()){
            holder.rvGallary.visibility = View.VISIBLE
            holder.rvGallary.layoutManager = GridLayoutManager(context, 3)
            holder.rvGallary.setHasFixedSize(true)
            val myAdapter = StudentDateWiseGalleryAdapter(modelList[position].Date,context, isShowCheckBox,this)
            holder.rvGallary.adapter = myAdapter
            myAdapter.modelList = modelList[position].studentGalleryDataList as MutableList<StudentGalleryData>
            myAdapter.notifyDataSetChanged()
        }else{
            holder.rvGallary.visibility = View.GONE
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentPortfolioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_gallery, parent, false)
        return StudentPortfolioViewHolder(itemView)
    }

    class StudentPortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val rvGallary : RecyclerView = itemView.findViewById(R.id.rv_gallary)
        val chkDate: CheckBox = itemView.findViewById(R.id.chk_date)

    }

    override fun onItemClick(item: StudentGalleryData) {
        listener.onItemClick(item)
    }

    override fun onCheckBoxClicked(date:String?,item: StudentGalleryData) {
        listener.onCheckBoxClicked(date,item)
    }

    override fun onDateCheckBoxClicked(item: StudentGalleryResponseItem) {
    }

}

interface IStudentGalleryClickListener{
    fun onItemClick(item: StudentGalleryData)
    fun onCheckBoxClicked(date: String?,item: StudentGalleryData)
    fun onDateCheckBoxClicked(item:StudentGalleryResponseItem)

}