package com.tce.teacherapp.util.gallerypicker.model.interactor

import android.provider.MediaStore
import com.tce.teacherapp.util.gallerypicker.model.GalleryAlbums
import com.tce.teacherapp.util.gallerypicker.model.GalleryData
import com.tce.teacherapp.util.gallerypicker.presenter.StudentProfilePhotosPresenterImpl
import com.tce.teacherapp.util.gallerypicker.utils.MLog
import java.io.File

class StudentProfilePhotosInteractorImpl(var presenter: StudentProfilePhotosPresenterImpl) : PhotosInteractor {

    override fun getPhoneAlbums() {
        val galleryAlbums: ArrayList<GalleryAlbums> = ArrayList()
        val albumsNames: ArrayList<String> = ArrayList()

        val imagesProjection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.TITLE)
        val imagesQueryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imagescursor = presenter.photosFragment.requireContext().contentResolver.query(imagesQueryUri, imagesProjection, null, null, null)


        val videoProjection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION)
        val videoQueryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videocursor = presenter.photosFragment.requireContext().contentResolver.query(videoQueryUri, videoProjection, null, null, null)

        MLog.e("IMAGES", imagescursor!!.count.toString())

        try {
            if (imagescursor != null && imagescursor.count > 0) {
                if (imagescursor.moveToFirst()) {
                    val idColumn = imagescursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val dataColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val dateAddedColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                    val titleColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.TITLE)
                    do {
                        val id = imagescursor.getString(idColumn)
                        val data = imagescursor.getString(dataColumn)
                        val dateAdded = imagescursor.getString(dateAddedColumn)
                        val title = imagescursor.getString(titleColumn)
                        val galleryData = GalleryData()
                        galleryData.albumName = File(data).parentFile.name
                        galleryData.photoUri = data
                        galleryData.id = Integer.valueOf(id)
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        galleryData.dateAdded = dateAdded
//                        galleryData.thumbnail = getThumbnailPath(galleryData.id.toLong()) ?: ""
//                        if (galleryData.thumbnail.isNotEmpty()) {
                            if (albumsNames.contains(galleryData.albumName)) {
                                for (album in galleryAlbums) {
                                    if (album.name == galleryData.albumName) {
                                        galleryData.albumId = album.id
                                        album.albumPhotos.add(galleryData)
                                        presenter.photosFragment.photoList.add(galleryData)
                                        break
                                    }
                                }
                            } else {
                                val album = GalleryAlbums()
                                album.id = galleryData.id
                                galleryData.albumId = galleryData.id
                                album.name = galleryData.albumName
                                album.coverUri = galleryData.photoUri
                                album.albumPhotos.add(galleryData)
                                presenter.photosFragment.photoList.add(galleryData)
                                galleryAlbums.add(album)
                                albumsNames.add(galleryData.albumName)
                            }
//                        }
                    } while (imagescursor.moveToNext())
                }
                imagescursor.close()
            } else presenter.photosFragment.listener.onError()


            if (videocursor != null && videocursor.count > 0) {
                if (videocursor.moveToFirst()) {
                    val idColumn = videocursor.getColumnIndex(MediaStore.Video.Media._ID)
                    val dataColumn = videocursor.getColumnIndex(MediaStore.Video.Media.DATA)
                    val dateAddedColumn = videocursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                    val titleColumn = videocursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                    val durationColumn = videocursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                    do {
                        val id = videocursor.getString(idColumn)
                        val data = videocursor.getString(dataColumn)
                        val dateAdded = videocursor.getString(dateAddedColumn)
                        val title = videocursor.getString(titleColumn)
                        val duration = videocursor.getInt(durationColumn)
                        val galleryData = GalleryData()
                        galleryData.albumName = File(data).parentFile.name
                        galleryData.photoUri = data
                        galleryData.id = Integer.valueOf(id)
                        galleryData.duration = duration
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                        galleryData.dateAdded = dateAdded
                        if (albumsNames.contains(galleryData.albumName)) {
                            for (album in galleryAlbums) {
                                if (album.name == galleryData.albumName) {
                                    galleryData.albumId = album.id
                                    album.albumPhotos.add(galleryData)
                                    presenter.photosFragment.photoList.add(galleryData)
                                    break
                                }
                            }
                        } else {
                            val album = GalleryAlbums()
                            album.id = galleryData.id
                            galleryData.albumId = galleryData.id
                            album.name = galleryData.albumName
                            album.coverUri = galleryData.photoUri
                            album.albumPhotos.add(galleryData)
                            presenter.photosFragment.photoList.add(galleryData)
                            galleryAlbums.add(album)
                            albumsNames.add(galleryData.albumName)
                        }
                    } while (videocursor.moveToNext())
                }
                videocursor.close()
            } else presenter.photosFragment.listener.onError()
        } catch (e: Exception) {
            MLog.e("IMAGE PICKER", e.toString())
        } finally {
            presenter.photosFragment.listener.onComplete(galleryAlbums)
        }
    }

    override fun getPhonePhotos() {
        val galleryAlbums: ArrayList<GalleryAlbums> = ArrayList()
        val albumsNames: ArrayList<String> = ArrayList()

        val imagesProjection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.TITLE)
        val imagesQueryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imagescursor = presenter.photosFragment.requireContext().contentResolver.query(imagesQueryUri, imagesProjection, null, null, null)

        MLog.e("IMAGES", imagescursor!!.count.toString())

        try {
            if (imagescursor != null && imagescursor.count > 0) {
                if (imagescursor.moveToFirst()) {
                    val idColumn = imagescursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val dataColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val dateAddedColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                    val titleColumn = imagescursor.getColumnIndex(MediaStore.Images.Media.TITLE)
                    do {
                        val id = imagescursor.getString(idColumn)
                        val data = imagescursor.getString(dataColumn)
                        val dateAdded = imagescursor.getString(dateAddedColumn)
                        val title = imagescursor.getString(titleColumn)
                        val galleryData = GalleryData()
                        galleryData.albumName = File(data).parentFile.name
                        galleryData.photoUri = data
                        galleryData.id = Integer.valueOf(id)
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        galleryData.dateAdded = dateAdded
//                        galleryData.thumbnail = getThumbnailPath(galleryData.id.toLong()) ?: ""
//                        if (galleryData.thumbnail.isNotEmpty()) {
                        if (albumsNames.contains(galleryData.albumName)) {
                            for (album in galleryAlbums) {
                                if (album.name == galleryData.albumName) {
                                    galleryData.albumId = album.id
                                    album.albumPhotos.add(galleryData)
                                    presenter.photosFragment.photoList.add(galleryData)
                                    break
                                }
                            }
                        } else {
                            val album = GalleryAlbums()
                            album.id = galleryData.id
                            galleryData.albumId = galleryData.id
                            album.name = galleryData.albumName
                            album.coverUri = galleryData.photoUri
                            album.albumPhotos.add(galleryData)
                            presenter.photosFragment.photoList.add(galleryData)
                            galleryAlbums.add(album)
                            albumsNames.add(galleryData.albumName)
                        }
//                        }
                    } while (imagescursor.moveToNext())
                }
                imagescursor.close()
            } else presenter.photosFragment.listener.onError()
        } catch (e: Exception) {
            MLog.e("IMAGE PICKER", e.toString())
        } finally {
            presenter.photosFragment.listener.onComplete(galleryAlbums)
        }
    }

    override fun getPhoneVideos() {
        val galleryAlbums: ArrayList<GalleryAlbums> = ArrayList()
        val albumsNames: ArrayList<String> = ArrayList()

        val videoProjection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION)

        val videoQueryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val videocursor = presenter.photosFragment.requireContext().contentResolver.query(videoQueryUri, videoProjection, null, null, null)

        MLog.e("VIDEOS", videocursor!!.count.toString())

        try {
            if (videocursor != null && videocursor.count > 0) {
                if (videocursor.moveToFirst()) {
                    val idColumn = videocursor.getColumnIndex(MediaStore.Video.Media._ID)
                    val dataColumn = videocursor.getColumnIndex(MediaStore.Video.Media.DATA)
                    val dateAddedColumn = videocursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                    val titleColumn = videocursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                    val durationColumn = videocursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                    do {
                        val id = videocursor.getString(idColumn)
                        val data = videocursor.getString(dataColumn)
                        val dateAdded = videocursor.getString(dateAddedColumn)
                        val title = videocursor.getString(titleColumn)
                        val duration = videocursor.getInt(durationColumn)
                        val galleryData = GalleryData()
                        galleryData.albumName = File(data).parentFile.name
                        galleryData.photoUri = data
                        galleryData.id = Integer.valueOf(id)
                        galleryData.duration = duration
                        galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                        galleryData.dateAdded = dateAdded
                        if (albumsNames.contains(galleryData.albumName)) {
                            for (album in galleryAlbums) {
                                if (album.name == galleryData.albumName) {
                                    galleryData.albumId = album.id
                                    album.albumPhotos.add(galleryData)
                                    presenter.photosFragment.photoList.add(galleryData)
                                    break
                                }
                            }
                        } else {
                            val album = GalleryAlbums()
                            album.id = galleryData.id
                            galleryData.albumId = galleryData.id
                            album.name = galleryData.albumName
                            album.coverUri = galleryData.photoUri
                            album.albumPhotos.add(galleryData)
                            presenter.photosFragment.photoList.add(galleryData)
                            galleryAlbums.add(album)
                            albumsNames.add(galleryData.albumName)
                        }
                    } while (videocursor.moveToNext())
                }
                videocursor.close()
            } else presenter.photosFragment.listener.onError()
        } catch (e: Exception) {
            MLog.e("IMAGE PICKER", e.toString())
        } finally {
            presenter.photosFragment.listener.onComplete(galleryAlbums)
        }
    }

}