package com.example.gabriel.soundrecorder.player

import androidx.lifecycle.ViewModel

class RecordingViewModel(val recordingRepository: RecordingRepository): ViewModel(){

    fun getRecordings() = recordingRepository.getRecordings()
}