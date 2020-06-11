package com.tce.teacherapp.util

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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


    }



}