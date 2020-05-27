package com.tce.teacherapp.di

import android.app.Application
import com.tce.teacherapp.api.FakeApiService
import com.tce.teacherapp.repository.FakeMainRepositoryImpl
import com.tce.teacherapp.ui.DetailFragmentTest
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Singleton
@Component(
    modules = [
        TestFragmentModule::class,
        TestViewModelModule::class,
        TestAppModule::class

])
interface TestAppComponent :AppComponent {
    val apiService: FakeApiService
    val mainRepository: FakeMainRepositoryImpl

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun build(): TestAppComponent
    }

    fun inject(listFragmentTests: DetailFragmentTest)

}