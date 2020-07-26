package com.tce.teacherapp.ui.dashboard.planner

import com.tce.teacherapp.db.entity.*
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
        data.lessonPlanList?.let {
            setPlannerList(it)
        }

        data.eventList?.let {
            setEventList(it)
        }

        data.birthdayList?.let {
            setBirthdayList(it)
        }

        data.dailyPlannerList?.let {
            setDailyPlannerList(it)
        }

        data.eventData?.let {
            setEventList(it)
        }

        data.monthlyPlannerList?.let {
            setMonthlyPlannerList(it)
        }

        data.childList?.let {
            setChildList(it)
        }


    }

    private fun setChildList(list: ArrayList<Student>) {
        val update = getCurrentViewStateOrNew()
        update.childList = list
        setViewState(update)
    }


    private fun setEventList(event: EventData) {
        val update = getCurrentViewStateOrNew()
        update.eventData = event
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

    private fun setPlannerList(list: List<LessonPlan>) {
        val update = getCurrentViewStateOrNew()
        update.lessonPlanList = list
        setViewState(update)
    }

    private fun setEventList(list: List<Event>) {
        val update = getCurrentViewStateOrNew()
        update.eventList = list
        setViewState(update)
    }

    private fun setBirthdayList(list: List<StudentBirthDay>) {
        val update = getCurrentViewStateOrNew()
        update.birthdayList = list
        setViewState(update)
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<PlannerViewState>> = when (stateEvent) {

            is PlannerStateEvent.GetPlannerData -> {
               mainRepository.getPlannerData(stateEvent.query,stateEvent = stateEvent)
            }

            is PlannerStateEvent.GetPlannerEvent -> {
                mainRepository.getPlannerEventList(stateEvent.count,stateEvent.showOriginal,stateEvent = stateEvent)
            }

            is PlannerStateEvent.GetMonthlyPlannerData -> {
                mainRepository.getMonthlyPlannerData(stateEvent.query,stateEvent = stateEvent)
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