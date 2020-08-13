package com.tce.teacherapp.util.gallerypicker.presenter

import com.tce.teacherapp.ui.dashboard.messages.GroupChatFragment
import com.tce.teacherapp.util.gallerypicker.model.interactor.VideosInteractorImpl

class VideosPresenterImpl(var videosFragment: GroupChatFragment): VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}