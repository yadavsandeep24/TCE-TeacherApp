package com.tce.teacherapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.repository.FakeLoginRepositoryImpl
import com.tce.teacherapp.repository.FakeMainRepositoryImpl
import com.tce.teacherapp.ui.dashboard.subjects.SubjectsViewModel
import com.tce.teacherapp.ui.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject


@FlowPreview
@Suppress("UNCHECKED_CAST")
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class FakeLoginViewModelFactory
@Inject
constructor(
    private val loginRepository: FakeLoginRepositoryImpl
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}















