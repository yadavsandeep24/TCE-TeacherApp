package com.tce.teacherapp.di

import androidx.fragment.app.FragmentFactory
import com.tce.teacherapp.fragments.login.FakeLoginFragmentFactory
import com.tce.teacherapp.fragments.main.FakeSubjectsFragmentFactory
import com.tce.teacherapp.viewmodels.FakeLoginViewModelFactory
import com.tce.teacherapp.viewmodels.FakeSubjectViewModelFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Named
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Module
object TestFragmentModule {

    @JvmStatic
    @Singleton
    @Provides
    @Named("LoginFragmentFactory")
    fun provideLoginFragmentFactory(
        fakeLoginViewModelFactory: FakeLoginViewModelFactory
    ): FragmentFactory {
        return FakeLoginFragmentFactory(
            fakeLoginViewModelFactory
        )
    }

    @FlowPreview
    @JvmStatic
    @Singleton
    @Provides
    @Named("SubjectsFragmentFactory")
    fun provideSubjectFragmentFactory(
        viewModelFactory: FakeSubjectViewModelFactory
    ): FragmentFactory {
        return FakeSubjectsFragmentFactory(
            viewModelFactory
        )
    }
}








