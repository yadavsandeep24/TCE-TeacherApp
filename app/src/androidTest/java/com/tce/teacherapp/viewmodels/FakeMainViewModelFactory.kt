package com.tce.teacherapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.repository.FakeMainRepositoryImpl
import com.tce.teacherapp.ui.dashboard.subjects.SubjectsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class FakeMainViewModelFactory
@Inject
constructor(
    private val mainRepository: FakeMainRepositoryImpl
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectsViewModel::class.java)) {
            return SubjectsViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}















