package com.tce.teacherapp.ui.dashboard.planner.listeners

import com.tce.teacherapp.db.entity.LessonPlanPeriod

interface LessonPlanClickListener {
    fun onLessonPlanClick(lessonPlanPeriod: LessonPlanPeriod)
}