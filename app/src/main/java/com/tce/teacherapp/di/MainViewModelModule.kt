package com.tce.teacherapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.di.keys.MainViewModelKey
import com.tce.teacherapp.ui.dashboard.subjects.SubjectsViewModel
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
    @MainViewModelKey(SubjectsViewModel::class)
    abstract fun bindSubjectViewModel(subjectViewModel: SubjectsViewModel): ViewModel
}








