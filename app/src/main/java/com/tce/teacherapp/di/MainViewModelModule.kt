package com.tce.teacherapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.di.keys.MainViewModelKey
import com.tce.teacherapp.ui.dashboard.messages.MessageViewModel
import com.tce.teacherapp.ui.dashboard.subjects.SubjectsViewModel
import com.tce.teacherapp.ui.login.LoginViewModel
import com.tce.teacherapp.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    abstract fun bindMainViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MainViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(SubjectsViewModel::class)
    abstract fun bindSubjectViewModel(subjectViewModel: SubjectsViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(MessageViewModel::class)
    abstract fun bindMessageViewModel(messageViewModel: MessageViewModel): ViewModel
}








