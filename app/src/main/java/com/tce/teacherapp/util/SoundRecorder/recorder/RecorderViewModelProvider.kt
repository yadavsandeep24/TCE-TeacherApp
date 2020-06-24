package com.example.gabriel.soundrecorder.recorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RecorderViewModelProvider(val recorderRepository: RecorderRepository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RecorderViewModel(recorderRepository) as T
    }
}