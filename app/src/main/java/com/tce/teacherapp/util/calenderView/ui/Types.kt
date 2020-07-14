package com.tce.teacherapp.util.calenderView.ui

import android.view.View
import com.tce.teacherapp.util.calenderView.model.CalendarDay
import com.tce.teacherapp.util.calenderView.model.CalendarMonth

open class ViewContainer(val view: View)

interface DayBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, day: CalendarDay)
}

interface MonthHeaderFooterBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, month: CalendarMonth)
}

typealias MonthScrollListener = (CalendarMonth) -> Unit
