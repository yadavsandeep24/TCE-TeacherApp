package com.tce.teacherapp.util.gallerypicker.view

import android.content.Context
import com.tce.teacherapp.util.gallerypicker.model.GalleryAlbums
import com.tce.teacherapp.util.gallerypicker.model.GalleryData

interface ImagePickerContract {
    fun initRecyclerViews()
    fun galleryOperation(type: Int)
    fun toggleDropdown()
    fun getPhoneAlbums(
        context: Context,
        listener: OnPhoneImagesObtained,
        type: Int
    )
    fun updateTitle(galleryAlbums: GalleryAlbums = GalleryAlbums())
    fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData> = ArrayList())
}