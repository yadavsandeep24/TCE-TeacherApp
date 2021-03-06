package com.tce.teacherapp.ui.dashboard.subjects.adapter

import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.helpers.KotlinEpoxyHolder
import com.tce.teacherapp.util.glide.GlideApp
import com.tce.teacherapp.util.glide.SvgSoftwareLayerSetter

// This more traditional style uses an Epoxy view holder pattern.
// The KotlinHolder is used to cache the view look ups, but uses property delegates to simplify it.
// The annotations allow for code generation of a subclass, which has equals/hashcode, and some other
// helpers. An extension function is also generated to make it easier to use this in an EpoxyController.
@EpoxyModelClass(layout = R.layout.list_item_chapter)
abstract class ChapterListEpoxyHolder : EpoxyModelWithHolder<ChapterListHolder>() {

    @EpoxyAttribute
    lateinit var listener: () -> Unit
    @EpoxyAttribute
    lateinit var title: String
    @EpoxyAttribute
    lateinit var imageUrl: GlideUrl
    @EpoxyAttribute
    lateinit var imageDrawable: Drawable

    override fun bind(holder: ChapterListHolder) {
        holder.tvTitle.text = title
        holder.cardMain.setOnClickListener { listener() }
       // holder.imageView.background = imageDrawable
        Log.d("SAN","imageUrl.toStringUrl()-->"+imageUrl.toStringUrl())
        if(imageUrl.toStringUrl().contains(".svg")) {
            val requestBuilder = Glide.with(holder.imageView.context)
                .`as`(PictureDrawable::class.java)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(SvgSoftwareLayerSetter())
            requestBuilder.load(imageUrl).into(holder.imageView)
        }else{
            GlideApp.with(holder.imageView.context).load(imageUrl).into(holder.imageView)
        }

    }
}

class ChapterListHolder : KotlinEpoxyHolder() {
    val cardMain by bind<com.google.android.material.card.MaterialCardView>(R.id.card_chapter_book)
    val tvTitle by bind<TextView>(R.id.tv_chapter_book_title)
    val imageView by bind<AppCompatImageView>(R.id.iv_chapter_book)
}
