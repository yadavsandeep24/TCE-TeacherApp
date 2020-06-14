package com.picker.gallery.view

import com.picker.gallery.model.GalleryAlbums

interface OnPhoneImagesObtained {
    fun onComplete(albums: ArrayList<GalleryAlbums>)
    fun onError()
}
