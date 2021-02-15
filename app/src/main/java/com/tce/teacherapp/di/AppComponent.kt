package com.tce.teacherapp.di

import android.app.Application
import com.tce.teacherapp.api.receivers.AlarmReceiver
import com.tce.teacherapp.ui.BaseActivity
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.login.LauncherActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        MainViewModelModule::class,
        MainFragmentsModule::class
    ]
)
interface AppComponent {


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun injectLauncherActivity(launcherActivity: LauncherActivity)
    fun inject(baseActivity: BaseActivity)
    fun injectDashBoardActivity(dashboardActivity: DashboardActivity)
     fun injectBroadCastReceiver(alarmReceiver: AlarmReceiver)




}








