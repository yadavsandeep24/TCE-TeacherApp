package com.tce.teacherapp.util.gallerypicker.presenter

import com.tce.teacherapp.ui.dashboard.messages.MessageDetailFragment
import com.tce.teacherapp.util.gallerypicker.model.interactor.MessageDetailPhotosInteractorImpl

class MessageDetailPhotosPresenterImpl(var messageDetailFragment: MessageDetailFragment): PhotosPresenter {
    val interactor: MessageDetailPhotosInteractorImpl = MessageDetailPhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }

    override fun getPhonePhotos() {
    }

    override fun getPhoneVideos() {
    }
}