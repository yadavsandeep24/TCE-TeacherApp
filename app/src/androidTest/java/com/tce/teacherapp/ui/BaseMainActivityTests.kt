package com.tce.teacherapp.ui

import com.tce.teacherapp.TestTCEApplication
import com.tce.teacherapp.api.FakeApiService
import com.tce.teacherapp.di.TestAppComponent
import com.tce.teacherapp.repository.FakeMainRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * All tests extend this base class for easy configuration of fake Repository
 * and fake ApiService.
 */
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
abstract class BaseMainActivityTests{

    fun configureFakeApiService(
        gradesDataSource: String? = null,
        networkDelay: Long? = null,
        application: TestTCEApplication
    ): FakeApiService {
        val apiService = (application.appComponent as TestAppComponent).apiService
        gradesDataSource?.let { apiService.gradesJsonFileName = it }
        networkDelay?.let { apiService.networkDelay = it }
        return apiService
    }

    fun configureFakeRepository(
        apiService: FakeApiService,
        application: TestTCEApplication
    ): FakeMainRepositoryImpl {
        val mainRepository = (application.appComponent as TestAppComponent).mainRepository
        mainRepository.apiService = apiService
        return mainRepository
    }


    abstract fun injectTest(application: TestTCEApplication)
}














