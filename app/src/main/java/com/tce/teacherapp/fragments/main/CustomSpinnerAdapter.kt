package com.tce.teacherapp.fragments.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tce.teacherapp.R
import com.tce.teacherapp.db.entity.Grade


class CustomSpinnerAdapter(context: Context, resouceId: Int, textviewId: Int, list: List<Grade?>) :
    ArrayAdapter<Grade?>(context, resouceId, textviewId, list) {

    private var flater: LayoutInflater? = null


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowview(convertView, position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return rowview(convertView, position, parent)
    }

    private fun rowview(
        convertView: View?,
        position: Int,
        parent: ViewGroup?
    ): View {
        val rowItem: Grade? = getItem(position)
        val holder: ViewHolder
        var rowview: View? = convertView
        if (rowview == null) {
            holder = ViewHolder()
            flater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowview = flater!!.inflate(R.layout.spinner_dropdown, parent, false)
            holder.txtTitle = rowview.findViewById(R.id.text1)
            rowview?.tag = holder
        } else {
            holder = rowview.tag as ViewHolder
        }
        holder.txtTitle?.text = rowItem?.gradeTitle
        return rowview!!
    }

    private inner class ViewHolder {
        var txtTitle: TextView? = null
    }
}