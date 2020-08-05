package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.edgedevstudio.example.recyclerviewmultiselect.MainInterface
import com.edgedevstudio.example.recyclerviewmultiselect.ViewHolderClickListener
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.students.FeedbackFragment
import java.util.*


class FeedbackAdapter(val context: Context, val mainInterface: MainInterface) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>(),
    ViewHolderClickListener {
    override fun onLongTap(index: Int) {
        if (!FeedbackFragment.isMultiSelectOn) {
            FeedbackFragment.isMultiSelectOn = true
        }
        addIDIntoSelectedIds(index)
    }

    override fun onTap(index: Int) {
        if (FeedbackFragment.isMultiSelectOn) {
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
        if (selectedIds.size < 1) FeedbackFragment.isMultiSelectOn = false
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
            Log.d(FeedbackFragment.TAG, "The ID is $selectedItemID")
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

            FeedbackFragment.isMultiSelectOn = false
        }
    }


    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder?.tvFeedback?.setText(modelList[position].name)

        val id = modelList[position].id

        if (selectedIds.contains(id.toString())) {
            //if item is selected then,set foreground color of FrameLayout.
           holder?.tvFeedback?.background = context.resources.getDrawable(R.drawable.feedback_selected)
        } else {
            //else remove selected item color.
           holder?.tvFeedback?.background = context.resources.getDrawable(R.drawable.feedback_bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val itemView = inflater.inflate(R.layout.list_item_feedback, parent, false)
        return FeedbackViewHolder(itemView, this)
    }

    class FeedbackViewHolder(itemView: View, val r_tap: ViewHolderClickListener) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener, View.OnClickListener {

        val tvFeedback: TextView


        init {
            tvFeedback = itemView.findViewById(R.id.tvFeedback)
            tvFeedback.setOnClickListener(this)
            tvFeedback.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            r_tap.onTap(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            r_tap.onLongTap(adapterPosition)
            return true
        }
    }


}