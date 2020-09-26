package com.tce.teacherapp.util.gallerypicker.presenter

import android.os.Build
import androidx.annotation.RequiresApi
import com.tce.teacherapp.ui.dashboard.messages.MessageDetailFragment
import com.tce.teacherapp.util.gallerypicker.model.interactor.MessageDetailVideosInteractorImpl

class MessageDetailVideosPresenterImpl(var messageDetailFragment: MessageDetailFragment): VideosPresenter {
    var interactor = MessageDetailVideosInteractorImpl(this)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}