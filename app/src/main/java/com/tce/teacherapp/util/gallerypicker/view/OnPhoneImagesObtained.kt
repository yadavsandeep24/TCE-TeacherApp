package com.tce.teacherapp.util.gallerypicker.view

import com.tce.teacherapp.util.gallerypicker.model.GalleryAlbums

interface OnPhoneImagesObtained {
    fun onComplete(albums: ArrayList<GalleryAlbums>)
    fun onError()
}
