package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.ui.dashboard.students.StudentListFragment
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*

/**
 * Created by OPEYEMI OLORUNLEKE on 3/4/2018.
 */
@FlowPreview
@ExperimentalCoroutinesApi
class PortfolioAdapter(val context: Context, val listener: ViewHolderClickListener) :
    RecyclerView.Adapter<PortfolioViewHolder>() {
    private var isShowCheckBox: Boolean = false

    fun setIsShowCheckBox(isShow: Boolean) {
        isShowCheckBox = isShow
        notifyDataSetChanged()
    }

    fun setIsSelectAll(isSelect: Boolean) {
        for (item in modelList) {
            item.isSelected = isSelect
        }
        notifyDataSetChanged()
    }


    fun addIDIntoSelectedIds(index: Int) {
        val id = modelList[index].id
        if (selectedIds.contains(id.toString())) {
            val selectedIndex = selectedIds.indexOf(id)
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

        holder.chkSelect.isChecked = modelList[position].isSelected

        holder.chkSelect.setOnCheckedChangeListener { _, isChecked ->
            modelList[position].isSelected = isChecked
            listener.onCheckBoxClicked(modelList[position])
        }

        val id = modelList[position].id

        if (selectedIds.contains(id)) {
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
        Glide.with(context)
            .load(modelList[position].ImagePath)
            .placeholder(R.drawable.ic_profile_icon_present)
            .into(holder.imgStudent)

        holder.mainContainer.setOnClickListener {
            if (StudentListFragment.isMultiSelectOn) {
                addIDIntoSelectedIds(position)
            } else {
                listener.onTap(position, modelList[position])
            }
        }
        holder.mainContainer.setOnLongClickListener {
            if (!StudentListFragment.isMultiSelectOn) {
                StudentListFragment.isMultiSelectOn = true
            }
            addIDIntoSelectedIds(position)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.portfolio_list_item, parent, false)
        return PortfolioViewHolder(itemView)
    }


}

class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvStudentName: TextView = itemView.findViewById(R.id.tv_student_name)
    val mainContainer: LinearLayout = itemView.findViewById(R.id.root_layout)
    val imgStudent = itemView.findViewById<AppCompatImageView>(R.id.img_student)
    val chkSelect: CheckBox = itemView.findViewById(R.id.chk_completed)
}