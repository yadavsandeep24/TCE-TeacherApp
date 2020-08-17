package com.tce.teacherapp.util.gallerypicker.presenter

import com.tce.teacherapp.ui.dashboard.messages.GroupChatFragment
import com.tce.teacherapp.util.gallerypicker.model.interactor.PhotosInteractorImpl

class PhotosPresenterImpl(var photosFragment: GroupChatFragment): PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }

    override fun getPhonePhotos() {
    }

    override fun getPhoneVideos() {
    }
}