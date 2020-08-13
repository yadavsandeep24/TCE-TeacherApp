package com.tce.teacherapp.util.gallerypicker.presenter

import com.tce.teacherapp.ui.dashboard.students.StudentProfileUploadResourceSelectionFragment
import com.tce.teacherapp.util.gallerypicker.model.interactor.StudentProfilePhotosInteractorImpl

class StudentProfilePhotosPresenterImpl(var photosFragment: StudentProfileUploadResourceSelectionFragment): PhotosPresenter {
    val interactor: StudentProfilePhotosInteractorImpl = StudentProfilePhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}