package com.tce.teacherapp.di

import android.app.Application
import com.tce.teacherapp.ui.BaseActivity
import com.tce.teacherapp.ui.auth.LauncherActivity
import com.tce.teacherapp.ui.dashboard.DashboardActivity
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

}








