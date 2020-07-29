package com.tce.teacherapp.ui.dashboard.planner.state

import com.tce.teacherapp.util.StateEvent

sealed class PlannerStateEvent : StateEvent {

    class GetPlannerData(
        val isShowLess: Boolean,
        val selectedDate: String?
    ) : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetPlannerData"
        }
    }

    class GetMonthlyPlannerData (
        val query: String
    ) : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting division info."
        }

        override fun toString(): String {
            return "GetMonthlyPlannerData"
        }
    }
    class SetChildSelectedPosition (
        val position: Int
    ) : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting SetChildSelectedPosition info."
        }

        override fun toString(): String {
            return "SetChildSelectedPosition"
        }
    }

    object GetChildSelectedPosition : PlannerStateEvent() {
        override fun errorInfo(): String {
            return "Error in getting GetChildSelectedPosition info."
        }

        override fun toString(): String {
            return "GetChildSelectedPosition"
        }
    }

}