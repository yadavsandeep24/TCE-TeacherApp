package com.tce.teacherapp.ui.dashboard.subjects.adapter

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.chapter_learn_practice_container)
abstract class PracticeEpoxyHolder : EpoxyModelWithHolder<PracticeHeaderHolder>() {


    override fun bind(holder: PracticeHeaderHolder) {
    }
}

class PracticeHeaderHolder : KotlinEpoxyHolder(){
}