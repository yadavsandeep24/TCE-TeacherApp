package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.FeedbackMasterDataItem
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.ui.dashboard.students.FeedbackFragment
import com.tce.teacherapp.ui.dashboard.students.interfaces.MainInterface
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*


@FlowPreview
@ExperimentalCoroutinesApi
class FeedbackAdapter(val context: Context, val mainInterface: MainInterface) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>(),
    ViewHolderClickListener {

    override fun onLongTap(index: Int) {
    }

    override fun onTap(index: Int,item : StudentListResponseItem?) {
            addIDIntoSelectedIds(index)
    }

    fun addIDIntoSelectedIds(index: Int) {
        val id = modelList[index].Id
        if (selectedIds.contains(id)) {
            val selectedIndex = selectedIds.indexOf(id)
            selectedIds.removeAt(selectedIndex)
        }
        else {
            selectedIds.add(id)
        }
        notifyItemChanged(index)
        if (selectedIds.size < 1) FeedbackFragment.isMultiSelectOn = false
        mainInterface.mainInterface(selectedIds.size)
    }

    var modelList: MutableList<FeedbackMasterDataItem> = ArrayList()
    val selectedIds: MutableList<String> = ArrayList()

    override fun getItemCount() = modelList.size



    fun deleteSelectedIds() {
        if (selectedIds.size < 1) return
        val selectedIdIteration = selectedIds.listIterator();

        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            Log.d(FeedbackFragment.TAG, "The ID is $selectedItemID")
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<FeedbackMasterDataItem> = modelList.listIterator();
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID.equals(model.Id,true)) {
                    modelListIteration.remove()
                    selectedIdIteration.remove()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }

            FeedbackFragment.isMultiSelectOn = false
        }
    }


    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.tvFeedback.text = modelList[position].Name

        val id = modelList[position].Id

        if (selectedIds.contains(id)) {
            //if item is selected then,set foreground color of FrameLayout.
           holder.tvFeedback.background = context.resources.getDrawable(R.drawable.feedback_selected)
        } else {
            //else remove selected item color.
           holder.tvFeedback.background = context.resources.getDrawable(R.drawable.feedback_bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_feedback, parent, false)
        return FeedbackViewHolder(itemView, this)
    }

    class FeedbackViewHolder(itemView: View, val r_tap: ViewHolderClickListener) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener, View.OnClickListener {

        val tvFeedback: TextView = itemView.findViewById(R.id.tvFeedback)


        init {
            tvFeedback.setOnClickListener(this)
            tvFeedback.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            r_tap.onTap(adapterPosition,null)
        }

        override fun onLongClick(v: View?): Boolean {
            r_tap.onLongTap(adapterPosition)
            return true
        }
    }


}