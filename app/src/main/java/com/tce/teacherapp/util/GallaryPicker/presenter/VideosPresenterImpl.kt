package com.picker.gallery.presenter

import com.picker.gallery.model.interactor.VideosInteractorImpl
import com.tce.teacherapp.ui.dashboard.messages.GroupChatFragment

class VideosPresenterImpl(var videosFragment: GroupChatFragment): VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}