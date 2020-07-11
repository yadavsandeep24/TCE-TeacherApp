package com.tce.teacherapp.ui.dashboard.planner.state

import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.util.StateEvent

sealed class PlannerStateEvent : StateEvent {

    class GetPlannerData (
        val query: String
    ) : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetDivisionEvent"
        }
    }

    class GetPlannerEvent(
        val count: Int,
        val showOriginal: Boolean
    ) : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting event info."
        }

        override fun toString(): String {
            return "GetDashboardEvent"
        }
    }


}