package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.ui.dashboard.students.StudentListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
@FlowPreview
@ExperimentalCoroutinesApi
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

    override fun onTap(index: Int,item: StudentListResponseItem?) {
        if (StudentListFragment.isMultiSelectOn) {
            addIDIntoSelectedIds(index)
        } else {
            listener.onTap(index,modelList[index])
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

    var modelList: MutableList<StudentListResponseItem> = ArrayList()
    val selectedIds: MutableList<String> = ArrayList()

    override fun getItemCount() = modelList.size

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.tvStudentName.text = modelList[position].Name

        holder.mainContainer.alpha = 1F
        holder.tvStudentName.alpha = 1F
        holder.imgStudent.alpha = 1F

        if (isShowCheckBox) {
            holder.chkSelect.visibility = View.VISIBLE
        } else {
            holder.chkSelect.visibility = View.GONE
        }

        holder.chkSelect.isChecked = isSelectAll

        val id = modelList[position].id

        if (selectedIds.contains(id.toString())) {
            //if item is selected then,set foreground color of FrameLayout.
            // holder?.mainContainer?.background = ColorDrawable(ContextCompat.getColor(context, R.color.dim_color))
            holder.mainContainer.alpha = 0.5F
            holder.imgStudent.alpha = 0.5F
            holder.tvStudentName.alpha = 0.5F
        } else {
            //else remove selected item color.
            // holder?.mainContainer?.background = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
            holder.mainContainer.alpha = 1F
            holder.tvStudentName.alpha = 1F
            holder.imgStudent.alpha = 1F
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.portfolio_list_item, parent, false)
        return PortfolioViewHolder(itemView, this)
    }


}