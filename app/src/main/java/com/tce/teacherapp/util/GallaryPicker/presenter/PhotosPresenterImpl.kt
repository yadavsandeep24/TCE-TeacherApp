package com.picker.gallery.presenter

import com.picker.gallery.model.interactor.PhotosInteractorImpl
import com.tce.teacherapp.ui.dashboard.messages.GroupChatFragment

class PhotosPresenterImpl(var photosFragment: GroupChatFragment): PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}