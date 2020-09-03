package com.tce.teacherapp.ui.dashboard.planner

import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.db.entity.DailyPlanner
import com.tce.teacherapp.db.entity.MonthlyPlanner
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerStateEvent
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class PlannerViewModel @Inject constructor(val mainRepository: MainRepository) :
    BaseViewModel<PlannerViewState>() {

    override fun handleNewData(data: PlannerViewState) {

        data.dailyPlannerList?.let {
            setDailyPlannerList(it)
        }

        data.monthlyPlannerList?.let {
            setMonthlyPlannerList(it)
        }

        data.childList?.let {
            setChildList(it)
        }


    }

    private fun setChildList(list: ArrayList<StudentListResponseItem>) {
        val update = getCurrentViewStateOrNew()
        update.childList = list
        setViewState(update)
    }

    private fun setMonthlyPlannerList(list: List<MonthlyPlanner>) {
        val update = getCurrentViewStateOrNew()
        update.monthlyPlannerList = list
        setViewState(update)
    }


    private fun setDailyPlannerList(list: List<DailyPlanner>) {
        val update = getCurrentViewStateOrNew()
        update.dailyPlannerList = list
        setViewState(update)
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<PlannerViewState>> = when (stateEvent) {

            is PlannerStateEvent.GetPlannerData -> {
               mainRepository.getPlannerData(stateEvent.isShowLess,stateEvent.selectedDate,stateEvent = stateEvent)
            }

            is PlannerStateEvent.GetMonthlyPlannerData -> {
                mainRepository.getMonthlyPlannerData(stateEvent.query,stateEvent = stateEvent)
            }

            is PlannerStateEvent.GetChildSelectedPosition ->{
                mainRepository.getSelectedChildPosition(stateEvent = stateEvent)
            }

            is PlannerStateEvent.SetChildSelectedPosition ->{
                mainRepository.setSelectedChildPosition(stateEvent.position,stateEvent = stateEvent)
            }

            else -> {
                flow{

                }
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): PlannerViewState {
        return PlannerViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}