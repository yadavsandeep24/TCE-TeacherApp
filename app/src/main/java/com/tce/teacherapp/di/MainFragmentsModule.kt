package com.tce.teacherapp.di

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.fragments.login.LoginFragmentFactory
import com.tce.teacherapp.fragments.main.MessagesFragmentFactory
import com.tce.teacherapp.fragments.main.SubjectsFragmentFactory
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


}