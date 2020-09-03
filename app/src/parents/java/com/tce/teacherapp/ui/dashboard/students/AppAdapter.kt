package com.tce.teacherapp.ui.dashboard.students

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tce.teacherapp.R

class AppAdapter(private val apps: List<App>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<AppAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val app = apps[viewHolder.adapterPosition]
        viewHolder.bind(app)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView
        private val appName: TextView
        private var app: App? = null
        fun bind(app: App) {
            this.app = app
            val packageManager = appName.context.packageManager
            appIcon.setImageDrawable(app.resolveInfo!!.loadIcon(packageManager))
            appName.text = app.resolveInfo!!.loadLabel(packageManager)
        }

        init {
            appIcon = itemView.findViewById(R.id.image_view_app_icon)
            appName = itemView.findViewById(R.id.text_view_app_name)
            itemView.setOnClickListener { v: View? -> listener.onItemClick(app) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(app: App?)
    }
}