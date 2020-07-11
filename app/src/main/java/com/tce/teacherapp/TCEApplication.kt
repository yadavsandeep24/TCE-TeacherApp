package com.tce.teacherapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tce.teacherapp.di.AppComponent
import com.tce.teacherapp.di.DaggerAppComponent

open class TCEApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
        // Initialize ThreeTenABP library
        AndroidThreeTen.init(this)
    }

    open fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}