package com.tce.teacherapp

import com.tce.teacherapp.di.DaggerTestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@UseExperimental(InternalCoroutinesApi::class)
class TestTCEApplication : TCEApplication() {

    override fun initAppComponent() {
        appComponent = DaggerTestAppComponent.builder()
            .application(this)
            .build()
    }



}










