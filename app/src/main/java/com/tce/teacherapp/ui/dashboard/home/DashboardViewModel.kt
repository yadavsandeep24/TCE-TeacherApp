package com.tce.teacherapp.ui.dashboard.home

import com.tce.teacherapp.db.entity.*
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
        data.isFingerPrintLoginEnabled?.let {
            setFingerPrintEnableData(it)
        }
        data.isFaceLoginEnabled?.let {
            setFaceIdEbableddata(it)
        }
        data.eventList?.let {
            setEventList(it)
        }

        data.todayResourceList?.let {
            setTodayResourceList(it)
        }

        data.lastViewedResourceList?.let {
            setLastViewedResourceList(it)
        }

        data.classList?.let {
            setuserClassData(it)
        }

        data.latestUpdateList?.let {
            setLatestUpdateList(it)
        }
    }

    private fun setLatestUpdateList(it: ArrayList<DashboardLatestUpdate>) {
        val update = getCurrentViewStateOrNew()
        update.latestUpdateList = it
        setViewState(update)
    }

    private fun setuserClassData(it: List<ClassListsItem>) {
        val update = getCurrentViewStateOrNew()
        update.classList = it
        setViewState(update)
    }

    private fun setFingerPrintEnableData(it: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.isFingerPrintLoginEnabled = it
        setViewState(update)
    }

    private fun setFaceIdEbableddata(it: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.isFaceLoginEnabled = it
        setViewState(update)
    }
    private fun setProfile(profile: Profile) {
        val update = getCurrentViewStateOrNew()
        update.profile = profile
        setViewState(update)
    }

    private fun setEventList(list: ArrayList<Event>) {
        val update = getCurrentViewStateOrNew()
        update.eventList = list
        setViewState(update)
    }

    private fun setTodayResourceList(list: ArrayList<DashboardResource>) {
        val update = getCurrentViewStateOrNew()
        update.todayResourceList = list
        setViewState(update)
    }

    private fun setLastViewedResourceList(list: ArrayList<DashboardResourceType>) {
        val update = getCurrentViewStateOrNew()
        update.lastViewedResourceList = list
        setViewState(update)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<DashboardViewState>> = when (stateEvent) {

            is DashboardStateEvent.GetProfileEvent -> {
                mainRepository.getProfile(stateEvent = stateEvent)
            }
            is DashboardStateEvent.UpdateProfilePic ->{
                mainRepository.updateProfilePic(stateEvent.resultUri,stateEvent = stateEvent)
            }

            is DashboardStateEvent.GetDashboardData -> {
                mainRepository.getDashboardData(stateEvent = stateEvent)
            }

            is DashboardStateEvent.GetDashboardEvent -> {
                mainRepository.getEventList(stateEvent.count,stateEvent = stateEvent)
            }

            is DashboardStateEvent.GetTodayResource -> {
                mainRepository.getTodayResourceList(stateEvent.count,stateEvent = stateEvent)
            }

            is DashboardStateEvent.GetLastViewedResource -> {
                mainRepository.getLastViewedResourceList(stateEvent.count,stateEvent = stateEvent)
            }
            is DashboardStateEvent.CheckLoginEnabledMode ->{
                mainRepository.checkLoginMode(stateEvent = stateEvent)
            }
            is DashboardStateEvent.FingerPrintEnableMode -> {
                mainRepository.setFingerPrintMode(stateEvent.checked,stateEvent =  stateEvent)
            }
            is DashboardStateEvent.FaceIdEnableMode -> {
                mainRepository.setFaceIdMode(stateEvent.checked,stateEvent =  stateEvent)
            }
            is DashboardStateEvent.UpdatePassword -> {
                mainRepository.updatePassword(stateEvent.oldPassword,stateEvent.newPassword,stateEvent.conFirmPassword,stateEvent = stateEvent)
            }
            is DashboardStateEvent.UpdateProfile -> {
               mainRepository.updateProfile(stateEvent.userInfo,stateMessage = stateEvent)
            }
            is DashboardStateEvent.GetUserClassList -> {
                mainRepository.getUserClassLists(stateEvent = stateEvent)
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