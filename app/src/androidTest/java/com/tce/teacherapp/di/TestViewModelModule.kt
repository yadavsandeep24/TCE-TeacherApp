package com.tce.teacherapp.di

import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.fragments.login.FakeLoginFragmentFactory
import com.tce.teacherapp.repository.FakeLoginRepositoryImpl
import com.tce.teacherapp.repository.FakeMainRepositoryImpl
import com.tce.teacherapp.viewmodels.FakeLoginViewModelFactory
import com.tce.teacherapp.viewmodels.FakeSubjectViewModelFactory
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
    fun provideLoginViewModelFactory(
        loginRepository: FakeLoginRepositoryImpl
    ): FakeLoginViewModelFactory{
        return FakeLoginViewModelFactory(
            loginRepository
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideViewModelFactory(
        mainRepository: FakeMainRepositoryImpl
    ): ViewModelProvider.Factory{
        return FakeSubjectViewModelFactory(
            mainRepository
        )
    }
}













