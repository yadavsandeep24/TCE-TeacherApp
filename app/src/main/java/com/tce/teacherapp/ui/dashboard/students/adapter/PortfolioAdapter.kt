package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edgedevstudio.example.recyclerviewmultiselect.ViewHolderClickListener
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.students.StudentListFragment
import java.util.*

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
class PortfolioAdapter(val context: Context, val listener: ViewHolderClickListener) : RecyclerView.Adapter<PortfolioViewHolder>(),
    ViewHolderClickListener {
    var isShowCheckBox: Boolean = false
    var isSelectAll: Boolean = false
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
            listener.onTap(index)
        }
    }

    fun setIsShowCheckBox(isShow: Boolean) {
        isShowCheckBox = isShow
        notifyDataSetChanged()
    }

    fun setIsSelectAll(isSelect: Boolean) {
        isSelectAll = isSelect
        notifyDataSetChanged()
    }


    fun addIDIntoSelectedIds(index: Int) {
        val id = modelList[index].id
        if (selectedIds.contains(id.toString())) {
            var selectedIndex = selectedIds.indexOf(id.toString())
            selectedIds.removeAt(selectedIndex)
        } else {
            selectedIds.add(id.toString())
        }
        notifyItemChanged(index)
        if (selectedIds.size < 1) StudentListFragment.isMultiSelectOn = false
        //  mainInterface.mainInterface(selectedIds.size)
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


    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder?.tvStudentName?.setText(modelList[position].name)

        holder?.mainContainer?.alpha = 1F
        holder?.tvStudentName?.alpha = 1F
        holder?.imgStudent.alpha = 1F

        if (isShowCheckBox) {
            holder?.chkSelect?.visibility = View.VISIBLE
        } else {
            holder?.chkSelect?.visibility = View.GONE
        }

        if (isSelectAll) {
            holder?.chkSelect?.isChecked = true
        } else {
            holder?.chkSelect?.isChecked = false
        }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.portfolio_list_item, parent, false)
        return PortfolioViewHolder(itemView, this)
    }


}