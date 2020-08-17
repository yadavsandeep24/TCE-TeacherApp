package com.tce.teacherapp.util.gallerypicker.presenter

import com.tce.teacherapp.ui.dashboard.students.StudentProfileUploadResourceSelectionFragment
import com.tce.teacherapp.util.gallerypicker.model.interactor.StudentProfilePhotosInteractorImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
class StudentProfilePhotosPresenterImpl @ExperimentalCoroutinesApi constructor(var photosFragment: StudentProfileUploadResourceSelectionFragment): PhotosPresenter {
    val interactor: StudentProfilePhotosInteractorImpl = StudentProfilePhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }

    override fun getPhonePhotos() {
        interactor.getPhonePhotos()
    }

    override fun getPhoneVideos() {
        interactor.getPhoneVideos()
    }
}