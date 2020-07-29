package com.tce.teacherapp.ui.dashboard.planner.listeners

import com.tce.teacherapp.db.entity.LessonPlanPeriod
import com.tce.teacherapp.db.entity.LessonPlanResource

interface LessonPlanClickListener {
    fun onLessonPlanClick(lessonPlanPeriod: LessonPlanPeriod)
    fun onMarkCompletedClick(lessonPlanPeriod: LessonPlanPeriod)
    fun onResourceMarkCompletedChecked(resource: LessonPlanResource, isChecked : Boolean)
    fun onLessonPlanResourceItemClick(resource: LessonPlanResource)
}