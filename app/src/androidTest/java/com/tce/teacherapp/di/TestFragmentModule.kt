package com.tce.teacherapp.di

import androidx.fragment.app.FragmentFactory
import com.tce.teacherapp.fragments.main.FakeMainFragmentFactory
import com.tce.teacherapp.viewmodels.FakeMainViewModelFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Module
object TestFragmentModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideMainFragmentFactory(
        viewModelFactory: FakeMainViewModelFactory
    ): FragmentFactory {
        return FakeMainFragmentFactory(
            viewModelFactory
        )
    }
}








