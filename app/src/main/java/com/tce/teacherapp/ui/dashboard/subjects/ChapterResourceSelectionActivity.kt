package com.tce.teacherapp.ui.dashboard.subjects

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.BaseActivity

class ChapterResourceSelectionActivity : BaseActivity() {
    override fun inject() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chapter_resource_selection_activity)

        val btnNext = findViewById<Button>(R.id.btn_nav_next)
        btnNext.setOnClickListener {
            val i = Intent(this,VideoPlayerActivity::class.java)
            startActivity(i)
        }
    }

    override fun displayProgressBar(isLoading: Boolean) {
    }

    override fun expandAppBar(value: Boolean) {
    }
}