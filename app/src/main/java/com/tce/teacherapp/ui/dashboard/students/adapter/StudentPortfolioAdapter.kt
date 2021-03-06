package com.tce.teacherapp.ui.dashboard.students.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Feedback
import com.tce.teacherapp.api.response.Portfolio
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.api.response.StudentGalleryResponseItem
import java.text.SimpleDateFormat
import java.util.*


class StudentPortfolioAdapter(
    val context: Context,
    val isShowCheckBox: Boolean,
    val listener: IStudentGalleryClickListener
) :
    RecyclerView.Adapter<StudentPortfolioAdapter.StudentPortfolioViewHolder>(), IFeedbackListener,
    IStudentGalleryClickListener {


    var modelList: MutableList<Portfolio> = ArrayList()

    override fun getItemCount() = modelList.size


    override fun onBindViewHolder(holder: StudentPortfolioViewHolder, position: Int) {
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)

        val date = simpleDateFormat.parse(modelList[position].Date)

        val formattedString: String = SimpleDateFormat("dd MMMM yyyy EEEE").format(date)
        holder.tvDate.text = formattedString
        if (position == 0) {
            holder.dotIndicator.background =
                context.resources.getDrawable(R.drawable.ic_portfolio_blue_dot)
        } else {
            holder.dotIndicator.background =
                context.resources.getDrawable(R.drawable.ic_portfolio_dark_dot)
        }

        if (modelList[position].Gallery != null && modelList[position].Gallery.isNotEmpty()) {
            holder.cardGallary.visibility = View.VISIBLE
            holder.rvGallary.layoutManager = GridLayoutManager(context, 2)
            holder.rvGallary.setHasFixedSize(true)
            val myAdapter = StudentPortfolioGalleryAdapter(
                modelList[position].Date,
                context,
                isShowCheckBox,
                this
            )
            holder.rvGallary.adapter = myAdapter
            myAdapter.modelList = modelList[position].Gallery as MutableList<StudentGalleryData>
            myAdapter.notifyDataSetChanged()
            if (isShowCheckBox) {
                holder.chkGallary.visibility = View.VISIBLE
                holder.chkGallary.isChecked = modelList[position].isGalleryCheckBoxSelected
                holder.chkGallary.setOnClickListener { v -> // you might keep a reference to the CheckBox to avoid this class cast
                    val checked = (v as CheckBox).isChecked
                        for (item in modelList[position].Gallery) {
                            item.isSelected = checked
                        }
                    modelList[position].isGalleryCheckBoxSelected = checked
                    notifyDataSetChanged()
                }

            } else {
                holder.chkGallary.visibility = View.GONE
            }
            if (modelList[position].Gallery.size > 4) {
                holder.viewAllContainer.visibility = View.VISIBLE
            } else {
                holder.viewAllContainer.visibility = View.GONE
            }
        } else {
            holder.chkGallary.visibility = View.GONE
            holder.cardGallary.visibility = View.GONE
        }

        if (modelList[position].Feedback != null && modelList[position].Feedback.isNotEmpty()) {
            holder.rvFeedback.visibility = View.VISIBLE
            holder.rvFeedback.layoutManager = GridLayoutManager(context, 1)
            holder.rvFeedback.setHasFixedSize(true)
            val myAdapter = StudentPortfolioFeedbackAdapter(context, isShowCheckBox, this)
            holder.rvFeedback.adapter = myAdapter
            myAdapter.modelList = modelList[position].Feedback as MutableList<Feedback>
            myAdapter.notifyDataSetChanged()
        } else {
            holder.rvFeedback.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(modelList[position].TeacherNote)) {
            holder.cardTeacherNote.visibility = View.VISIBLE
            holder.tvTeacherNote.text = modelList[position].TeacherNote
            if (isShowCheckBox) {
                holder.chkTeacherNote.visibility = View.VISIBLE
            } else {
                holder.chkTeacherNote.visibility = View.GONE
            }
        } else {
            holder.cardTeacherNote.visibility = View.GONE
            holder.chkTeacherNote.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentPortfolioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_item_portfolio, parent, false)
        return StudentPortfolioViewHolder(itemView)
    }

    class StudentPortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val dotIndicator: AppCompatImageView = itemView.findViewById(R.id.dot_indicator)
        val cardGallary: CardView = itemView.findViewById(R.id.card_gallary)
        val rvGallary: RecyclerView = itemView.findViewById(R.id.rv_gallary)
        val viewAllContainer: LinearLayout = itemView.findViewById(R.id.view_all_container)
        val rvFeedback: RecyclerView = itemView.findViewById(R.id.rv_feddback)
        val cardTeacherNote: CardView = itemView.findViewById(R.id.card_teacher_note)
        val tvTeacherNote: TextView = itemView.findViewById(R.id.tv_teacher_note)
        val chkGallary: CheckBox = itemView.findViewById(R.id.chk_gallary)
        val chkTeacherNote: CheckBox = itemView.findViewById(R.id.chk_teacher_note)


    }

    override fun onFeedbackCheckBoxClickListener(item: Feedback) {
/*        for (portFolio in modelList) {
            if (portFolio.Feedback.isNotEmpty()) {
                for (feedback in portFolio.Feedback) {
                    if (feedback.Id.equals(item.Id, true)) {
                        feedback.isSelected = item.isSelected
                        break
                    }
                }
            }
        }*/
    }

    override fun onItemClick(item: StudentGalleryData) {
       listener.onItemClick(item)
    }

    override fun onCheckBoxClicked(date: String?, item: StudentGalleryData) {
        for(portFolio in modelList){
            if(portFolio.Date.equals(date, true)){
                var isAllSelected = false
                for(galleryItem in portFolio.Gallery) {
                    isAllSelected = galleryItem.isSelected
                    if(!galleryItem.isSelected){
                        isAllSelected = false
                        break
                    }
                }
                portFolio.isGalleryCheckBoxSelected = isAllSelected
                notifyDataSetChanged()
                break
            }
        }
    }

    override fun onDateCheckBoxClicked(item: StudentGalleryResponseItem) {
    }


}