package com.tce.teacherapp.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URISyntaxException
import java.util.*


class Utility {

    companion object {

        fun getDrawable(name: String?, context: Context): Drawable? {
            Log.d("SAN", "name-->$name")
            val resourceId: Int = context.resources
                .getIdentifier(name, "drawable", context.packageName)
            return context.resources.getDrawable(resourceId, null)
        }

        fun getRealPathFromURI(context: Context, contentUri: Uri): String {
            var cursor: Cursor? = null
            try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri, proj, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(column_index)
            } finally {
                cursor?.close()
            }

        }

        fun copyFileFromSourceToDestn(sPath: String, dPath: String, isDeleteSourceDir: Boolean): Boolean {
            try {
                val fis = FileInputStream(File(sPath))
                val dir = File(dPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val outfile = File(dPath)
                outfile.delete()
                if (!outfile.exists()) {
                    outfile.createNewFile()
                }
                val fos = FileOutputStream(outfile)
                val block = ByteArray(1000)
                var i: Int = -1
                while ({ i = fis.read(block);i }() != -1) {
                    fos.write(block, 0, i)
                }
                fos.close()
                if (isDeleteSourceDir) {
                    deleteDir(File(sPath))
                }
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        fun deleteDir(dir: File): Boolean {
            if (dir.isDirectory) {
                val children = dir.list()
                for (item in children) {
                    deleteDir(File(dir, item))
                }
                return dir.delete()
            } else {
                if (dir.exists()) {
                    if (dir.delete()) {
                        Log.d("SAN", "file Deleted :" + dir.absolutePath)
                        return true
                    } else {
                        Log.d("SAN", "file note Deleted :" + dir.absolutePath)
                        return false
                    }
                }
                return false
            }
        }

        fun getUniqueID(userCode: String): String {
            return userCode + "-" + Date().time
        }

        @Throws(URISyntaxException::class)
        fun getFilePath(context: Context, uri: Uri): String? {
            val selection: String? = null
            val selectionArgs: Array<String>? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
                return getRealPathFromURI_API19(context, uri)
            }
            if ("content".equals(uri.scheme!!, ignoreCase = true)) {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        fun getRealPathFromURI_API19(context: Context, uri: Uri): String? {
            var filePath = ""
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    } else {

                        if (Build.VERSION.SDK_INT > 20) {
                            //getExternalMediaDirs() added in API 21
                            val extenal = context.externalMediaDirs
                            if (extenal.size > 1) {
                                filePath = extenal[1].absolutePath
                                filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1]
                            }
                        } else {
                            filePath = "/storage/" + type + "/" + split[1]
                        }
                        return filePath
                    }

                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                    var cursor: Cursor? = null
                    val column = "_data"
                    val projection = arrayOf(column)

                    try {
                        cursor = context.contentResolver.query(contentUri, projection, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            val index = cursor.getColumnIndexOrThrow(column)
                            val result = cursor.getString(index)
                            cursor.close()
                            return result
                        }
                    } finally {
                        cursor?.close()
                    }
                } else if (DocumentsContract.isDocumentUri(context, uri)) {
                    // MediaProvider
                    val wholeID = DocumentsContract.getDocumentId(uri)

                    // Split at colon, use second item in the array
                    val ids = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val id: String
                    val type: String
                    if (ids.size > 1) {
                        id = ids[1]
                        type = ids[0]
                    } else {
                        id = ids[0]
                        type = ids[0]
                    }

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(id)
                    val column = "_data"
                    val projection = arrayOf(column)
                    val cursor = context.contentResolver.query(contentUri!!,
                        projection, selection, selectionArgs, null)

                    if (cursor != null) {
                        val columnIndex = cursor.getColumnIndex(column)

                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(columnIndex)
                        }
                        cursor.close()
                    }
                    return filePath
                } else {
                    val proj = arrayOf(MediaStore.Audio.Media.DATA)
                    val cursor = context.contentResolver.query(uri, proj, null, null, null)
                    if (cursor != null) {
                        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                        if (cursor.moveToFirst())
                            filePath = cursor.getString(column_index)
                        cursor.close()
                    }


                    return filePath
                }
            }
            return null
        }

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }


    }



}