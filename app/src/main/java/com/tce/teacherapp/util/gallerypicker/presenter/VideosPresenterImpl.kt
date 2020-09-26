package com.tce.teacherapp.util.gallerypicker.presenter

import android.os.Build
import androidx.annotation.RequiresApi
import com.tce.teacherapp.ui.dashboard.messages.GroupChatFragment
import com.tce.teacherapp.util.gallerypicker.model.interactor.VideosInteractorImpl

class VideosPresenterImpl(var videosFragment: GroupChatFragment): VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}