package com.tce.teacherapp.di

import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.repository.FakeMainRepositoryImpl
import com.tce.teacherapp.viewmodels.FakeMainViewModelFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Module
object TestViewModelModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideViewModelFactory(
        mainRepository: FakeMainRepositoryImpl
    ): ViewModelProvider.Factory{
        return FakeMainViewModelFactory(
            mainRepository
        )
    }
}













