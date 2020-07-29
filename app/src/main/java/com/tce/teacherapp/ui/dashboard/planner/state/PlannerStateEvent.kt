package com.tce.teacherapp.ui.dashboard.planner.state

import com.tce.teacherapp.util.StateEvent

sealed class PlannerStateEvent : StateEvent {

    class GetPlannerData (
        val isShowLess: Boolean
    ) : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    class GetMonthlyPlannerData (
        val query: String
    ) : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }



}