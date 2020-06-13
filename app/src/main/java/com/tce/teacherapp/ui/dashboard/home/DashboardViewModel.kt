package com.tce.teacherapp.ui.dashboard.home

import com.tce.teacherapp.db.entity.Profile
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class DashboardViewModel @Inject constructor(val mainRepository: MainRepository) :
    BaseViewModel<DashboardViewState>() {

    override fun handleNewData(data: DashboardViewState) {
        data.profile?.let {
            setProfile(it)
        }
    }

    private fun setProfile(profile: Profile) {
        val update = getCurrentViewStateOrNew()
        update.profile = profile
        setViewState(update)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<DashboardViewState>> = when (stateEvent) {

            is DashboardStateEvent.GetProfileEvent -> {
                mainRepository.getProfile(stateEvent = stateEvent)
            }

            else -> {
                flow {

                }
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): DashboardViewState {
        return DashboardViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}