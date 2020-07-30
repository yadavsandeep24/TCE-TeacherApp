package com.tce.teacherapp.di

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.fragments.login.LoginFragmentFactory
import com.tce.teacherapp.fragments.main.*
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object MainFragmentsModule {

    @JvmStatic
    @Provides
    @Named("LoginFragmentFactory")
    fun provideLoginFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return LoginFragmentFactory(
            viewModelFactory
        )
    }

    @JvmStatic
    @Provides
    @Named("DashboardFragmentFactory")
    fun provideDashboardFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return DashboardFragmentFactory(
            viewModelFactory
        )
    }

    @JvmStatic
    @Provides
    @Named("SubjectsFragmentFactory")
    fun provideSubjectListFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return SubjectsFragmentFactory(viewModelFactory)
    }

    @JvmStatic
    @Provides
    @Named("MessagesFragmentFactory")
    fun provideMessageListFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return MessagesFragmentFactory(viewModelFactory)
    }

    @JvmStatic
    @Provides
    @Named("PlannerFragmentFactory")
    fun providePlannerListFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return PlannerFragmentFactory(viewModelFactory)
    }

    @JvmStatic
    @Provides
    @Named("StudentsFragmentFactory")
    fun provideStudentListFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return StudentsFragmentFactory(viewModelFactory)
    }

}