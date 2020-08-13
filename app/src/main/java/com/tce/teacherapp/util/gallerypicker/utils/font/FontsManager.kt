package com.tce.teacherapp.util.gallerypicker.utils.font

import android.content.Context
import android.graphics.Typeface

class FontsManager(var context: Context) {
    fun getTypeface(font: String = ""): Typeface = Typeface.createFromAsset(context.assets,"fonts/$font")
}