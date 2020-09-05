package com.tce.teacherapp.util

import android.content.ContentUris
import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.tce.teacherapp.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


open class Utility {

    companion object {

        fun getDrawable(name: String?, context: Context): Drawable? {
            Log.d("SAN", "name-->$name")
            return try {
                val resourceId: Int = context.resources
                    .getIdentifier(name, "drawable", context.packageName)
                context.resources.getDrawable(resourceId, null)
            }catch (e: java.lang.Exception) {
                e.printStackTrace()
                null
            }
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
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                    context.applicationContext,
                    uri
                )) {
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
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(
                    context.applicationContext,
                    uri
                )) {
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
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )

                    var cursor: Cursor? = null
                    val column = "_data"
                    val projection = arrayOf(column)

                    try {
                        cursor = context.contentResolver.query(
                            contentUri,
                            projection,
                            null,
                            null,
                            null
                        )
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
                    val cursor = context.contentResolver.query(
                        contentUri!!,
                        projection, selection, selectionArgs, null
                    )

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
        fun getBannerDayMessage(context: Context): String? {
            val c = Calendar.getInstance()
            val timeOfDay = c[Calendar.HOUR_OF_DAY]
            var str = context.resources.getString(R.string.lbl_good_morning)
            when (timeOfDay) {
                in 0..11 -> {
                    str = context.resources.getString(R.string.lbl_good_morning)
                }
                in 12..15 -> {
                    str = context.resources.getString(R.string.lbl_good_afternoon)
                }
                in 16..20 -> {
                    str = context.resources.getString(R.string.lbl_good_evening)
                }
                in 21..23 -> {
                    str = context.resources.getString(R.string.lbl_good_evening)
                }
            }
            return str
        }

        fun getRectangleBorder(
            solidColor: Int,
            radius: FloatArray,
            strokeWidth: Int,
            strokeColor: Int
        ): GradientDrawable {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(solidColor)
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            gradientDrawable.cornerRadii = radius
            gradientDrawable.setStroke(strokeWidth, strokeColor)
            return gradientDrawable
        }

        fun setSelectorRoundedCorner(
            context: Context, v: View, Stroke: Int, PrimarySolidColor: Int,
            PressedSolidColor: Int, PrimaryBorderColor: Int, PressedBOrderColor: Int,
            radius: Int
        ) {
            val states = StateListDrawable()

            states.addState(
                intArrayOf(android.R.attr.state_pressed), getRectangleBorder(
                    context.resources
                        .getColor(PressedSolidColor),
                    floatArrayOf(
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat()
                    ),
                    Stroke,
                    context.resources.getColor(PressedBOrderColor)
                )
            )
            states.addState(
                intArrayOf(), getRectangleBorder(
                    context.resources.getColor(
                        PrimarySolidColor
                    ),
                    floatArrayOf(
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat()
                    ),
                    Stroke,
                    context.resources.getColor(PrimaryBorderColor)
                )
            )
            v.setBackgroundDrawable(states)

        }

        fun setSelectorRoundedCornerWithDynamicColor(
            context: Context, v: View, Stroke: Int, PrimarySolidColor: String,
            PressedSolidColor: String, PrimaryBorderColor: Int, PressedBOrderColor: Int,
            radius: Int
        ) {
            val states = StateListDrawable()

            states.addState(
                intArrayOf(android.R.attr.state_pressed), getRectangleBorder(
                    Color.parseColor(PressedSolidColor),
                    floatArrayOf(
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat()
                    ),
                    Stroke,
                    context.resources.getColor(PressedBOrderColor)
                )
            )
            states.addState(
                intArrayOf(), getRectangleBorder(
                    Color.parseColor(
                        PrimarySolidColor
                    ),
                    floatArrayOf(
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat(),
                        radius.toFloat()
                    ),
                    Stroke,
                    context.resources.getColor(PrimaryBorderColor)
                )
            )
            v.setBackgroundDrawable(states)

        }
        fun calculateNumberOfColumns(base: Int, context: Context): Int {
            var columns = base
            val screenSize = getScreenSizeCategory(context)
            if (screenSize == "small") {
                if (base != 1) {
                    columns -= 1
                }
            } else if (screenSize == "normal") {
                // Do nothing
            } else if (screenSize == "large") {
                columns += 2
            } else if (screenSize == "xlarge") {
                columns += 3
            }
            if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                columns = (columns * 1.5).toInt()
            }
            return columns
        }

        // Custom method to get screen current orientation
         fun getScreenOrientation(context: Context): String? {
            var orientation = "undefined"
            if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                orientation = "landscape"
            } else if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                orientation = "portrait"
            }
            return orientation
        }

        // Custom method to get screen size category
        protected fun getScreenSizeCategory(context: Context): String {
            val screenLayout: Int =
                context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
            return when (screenLayout) {
                Configuration.SCREENLAYOUT_SIZE_SMALL ->                 // small screens are at least 426dp x 320dp
                    "small"
                Configuration.SCREENLAYOUT_SIZE_NORMAL ->                 // normal screens are at least 470dp x 320dp
                    "normal"
                Configuration.SCREENLAYOUT_SIZE_LARGE ->                 // large screens are at least 640dp x 480dp
                    "large"
                Configuration.SCREENLAYOUT_SIZE_XLARGE ->                 // xlarge screens are at least 960dp x 720dp
                    "xlarge"
                else -> "undefined"
            }
        }

        fun downloadFile(stUrl: String, destnUrl: String): String {
            var str = "fail"
            try {
                val url = URL(stUrl)
                val c: HttpURLConnection = url.openConnection() as HttpURLConnection
                val response: Int = c.responseCode
                if (createDirIfNotExists(destnUrl)) {
                    val fileName = stUrl.substring(stUrl.lastIndexOf("/") + 1)
                    val outfile = File(destnUrl, fileName)
                    outfile.delete()
                    val f = FileOutputStream(File(destnUrl, fileName))
                    val input = c.inputStream
                    val inStream = BufferedInputStream(input, 1024 * 5)
                    val buffer = ByteArray(7 * 1024)
                    var len: Int
                    while (inStream.read(buffer).also { len = it } != -1) {
                        f.write(buffer, 0, len)
                    }
                    f.flush()
                    f.close()
                    input.close()
             /*       val outputFile = File(File(destnUrl), fileName)
                    val fos = FileOutputStream(outputFile)
                    val input = BufferedInputStream(url.openStream(), 8192)
                    val buffer = ByteArray(1024)
                    var len1 = 0
                    while ({ len1 = input.read(buffer); len1 }() != -1) {
                        fos.write(buffer, 0, len1)
                    }
                    // flushing output
                    fos.flush()

                    fos.close()
                    input.close()*/
                    str = destnUrl + fileName
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return str
        }
        private fun createDirIfNotExists(path: String): Boolean {
            var ret = true
            val file = File(path)
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    ret = false
                }
            }
            return ret
        }

         fun getAge(dobString: String): Int {
            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            try {
                date = sdf.parse(dobString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (date == null) return 0
            val dob = Calendar.getInstance()
            val today = Calendar.getInstance()
            dob.time = date
            val year = dob[Calendar.YEAR]
            val month = dob[Calendar.MONTH]
            val day = dob[Calendar.DAY_OF_MONTH]
            dob[year, month + 1] = day
            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
            return age
        }

        fun getAgeFromLong(birthday: String?): String? {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dobCal = Calendar.getInstance()
            dobCal.time = sdf.parse(birthday)
            val diffCal = Calendar.getInstance()
            diffCal.timeInMillis =
                Calendar.getInstance().timeInMillis - dobCal.timeInMillis
            var ageS = ""
            var age = diffCal[Calendar.YEAR] - 1970
            //Check if less than a year
            if (age == 0) {
                age = diffCal[Calendar.MONTH]
                //Check if less than a month
                if (age == 0) {
                    age = diffCal[Calendar.WEEK_OF_YEAR]
                    //Check if less than a week
                    if (age == 1) {
                        age = diffCal[Calendar.DAY_OF_YEAR]
                        ageS = (age - 1).toString() + " Days"
                    } else {
                        ageS = (age - 1).toString() + " Weeks"
                    }
                } else {
                    ageS = "$age Months"
                }
            } else {
                ageS = "$age Years"
            }
            return ageS
        }
    }



}