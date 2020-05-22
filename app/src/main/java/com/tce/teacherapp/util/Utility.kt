package com.tce.teacherapp.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log


class Utility {

    companion object {

        fun getDrawable(name: String?, context: Context): Drawable? {
            Log.d("SAN", "name-->$name")
            val resourceId: Int = context.resources
                .getIdentifier(name, "drawable", context.packageName)
            return context.resources.getDrawable(resourceId, null)
        }

    }


}