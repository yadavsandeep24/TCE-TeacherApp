package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.ui.dashboard.students.interfaces.MainInterface
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.ui.dashboard.students.StudentListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
class AttendanceAdapter(val context: Context, val mainInterface: MainInterface) : RecyclerView.Adapter<AttendanceViewHolder>(),
    ViewHolderClickListener {

    override fun onLongTap(index: Int) {
        if (!StudentListFragment.isMultiSelectOn) {
            StudentListFragment.isMultiSelectOn = true
        }
        addIDIntoSelectedIds(index)
    }

    override fun onTap(index: Int,item : StudentListResponseItem?) {
            addIDIntoSelectedIds(index)
    }

    fun addIDIntoSelectedIds(index: Int) {
        val id = modelList[index].id
        if (selectedIds.contains(id)) {
            val selectedIndex = selectedIds.indexOf(id)
            selectedIds.removeAt(selectedIndex)
        }
        else {
            selectedIds.add(id)
        }
        notifyItemChanged(index)
        if (selectedIds.size < 1) StudentListFragment.isMultiSelectOn = false
        mainInterface.mainInterface(selectedIds.size)
    }

    var modelList: MutableList<StudentListResponseItem> = ArrayList()
    val selectedIds: MutableList<String> = ArrayList()

    override fun getItemCount() = modelList.size



    fun deleteSelectedIds() {
        if (selectedIds.size < 1) return
        val selectedIdIteration = selectedIds.listIterator();

        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            Log.d("SAN", "The ID is $selectedItemID")
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<StudentListResponseItem> = modelList.listIterator()
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID.equals(model.id,true)) {
                    selectedIdIteration.remove()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }

            StudentListFragment.isMultiSelectOn = false
        }
    }


    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.tvStudentName.text = modelList[position].Name

        holder.mainContainer.alpha = 1F
        holder.tvStudentName.alpha = 1F
        holder.imgStudent.alpha = 1F

        val id = modelList[position].id

        if (selectedIds.contains(id)) {
            holder.flIndicator.background = holder.flIndicator.context.getDrawable(R.drawable.bg_student_profile_absent)
            holder.imgStudent.background = holder.flIndicator.context.getDrawable(R.drawable.ic_profile_icon_absent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvStudentName.setTextColor(holder.flIndicator.context.resources.getColor(R.color.light_grey_blue,null))
            }else{
                holder.tvStudentName.setTextColor(holder.flIndicator.context.resources.getColor(R.color.light_grey_blue))
            }
        } else {
            //else remove selected item color.
            holder.flIndicator.background = holder.flIndicator.context.getDrawable(R.drawable.bg_student_profile_present)
            holder.imgStudent.background = holder.flIndicator.context.getDrawable(R.drawable.ic_profile_icon_present)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvStudentName.setTextColor(holder.flIndicator.context.resources.getColor(R.color.dark,null))
            }else{
                holder.tvStudentName.setTextColor(holder.flIndicator.context.resources.getColor(R.color.dark))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.student_list_item, parent, false)
        return AttendanceViewHolder(itemView, this)
    }


}