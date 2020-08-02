package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.edgedevstudio.example.recyclerviewmultiselect.MainInterface
import com.edgedevstudio.example.recyclerviewmultiselect.ViewHolderClickListener
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.students.StudentListFragment
import java.util.*

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
class AttendanceAdapter(val context: Context, val mainInterface: MainInterface) : RecyclerView.Adapter<AttendanceViewHolder>(),
    ViewHolderClickListener {
    override fun onLongTap(index: Int) {
        if (!StudentListFragment.isMultiSelectOn) {
            StudentListFragment.isMultiSelectOn = true
        }
        addIDIntoSelectedIds(index)
    }

    override fun onTap(index: Int) {
        if (StudentListFragment.isMultiSelectOn) {
            addIDIntoSelectedIds(index)
        } else {
            Toast.makeText(context, "Clicked Item @ Position ${index + 1}", Toast.LENGTH_SHORT).show()
        }
    }

    fun addIDIntoSelectedIds(index: Int) {
        val id = modelList[index].id
        if (selectedIds.contains(id.toString())) {
            var selectedIndex = selectedIds.indexOf(id.toString())
            selectedIds.removeAt(selectedIndex)
        }
        else {
            selectedIds.add(id.toString())
        }
        notifyItemChanged(index)
        if (selectedIds.size < 1) StudentListFragment.isMultiSelectOn = false
        mainInterface.mainInterface(selectedIds.size)
    }

    var modelList: MutableList<Student> = ArrayList<Student>()
    val selectedIds: MutableList<String> = ArrayList<String>()

    override fun getItemCount() = modelList.size



    fun deleteSelectedIds() {
        if (selectedIds.size < 1) return
        val selectedIdIteration = selectedIds.listIterator();

        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            Log.d(StudentListFragment.TAG, "The ID is $selectedItemID")
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<Student> = modelList.listIterator();
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID.equals(model.id)) {
                    modelListIteration.remove()
                    selectedIdIteration.remove()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }

            StudentListFragment.isMultiSelectOn = false
        }
    }


    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder?.tvStudentName?.setText(modelList[position].name)

        holder?.mainContainer?.alpha = 1F
        holder?.tvStudentName?.alpha = 1F
        holder?.imgStudent.alpha = 1F

        val id = modelList[position].id

        if (selectedIds.contains(id.toString())) {
            //if item is selected then,set foreground color of FrameLayout.
           // holder?.mainContainer?.background = ColorDrawable(ContextCompat.getColor(context, R.color.dim_color))
            holder?.mainContainer?.alpha = 0.5F
            holder?.imgStudent.alpha = 0.5F
            holder?.tvStudentName?.alpha = 0.5F
        } else {
            //else remove selected item color.
           // holder?.mainContainer?.background = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
            holder?.mainContainer?.alpha = 1F
            holder?.tvStudentName?.alpha = 1F
            holder?.imgStudent.alpha = 1F
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.student_list_item, parent, false)
        return AttendanceViewHolder(itemView, this)
    }


}