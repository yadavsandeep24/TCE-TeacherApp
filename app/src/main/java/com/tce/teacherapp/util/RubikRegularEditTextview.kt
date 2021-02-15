package com.tce.teacherapp.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet


class RubikRegularEditTextview : androidx.appcompat.widget.AppCompatEditText {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        val tf = Typeface.createFromAsset(context.assets, "fonts/Rubik-Regular.ttf")
        setTypeface(tf, Typeface.NORMAL)
    }
}