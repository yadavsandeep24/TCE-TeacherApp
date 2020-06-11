package com.tce.teacherapp.util

import android.graphics.drawable.AnimationDrawable
import android.os.Handler

abstract class CustomAnimationDrawableNew(aniDrawable: AnimationDrawable):AnimationDrawable() {
    /** Handles the animation callback. */
    internal lateinit var mAnimationHandler: Handler
    /**
     * Gets the total duration of all frames.
     *
     * @return The total duration.
     */
    val totalDuration:Int
        get() {
            var iDuration = 0
            for (i in 0 until this.getNumberOfFrames())
            {
                iDuration += this.getDuration(i)
            }
            return iDuration
        }
    init{
        /* Add each frame to our animation drawable */
        for (i in 0 until aniDrawable.getNumberOfFrames())
        {
            this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i))
        }
    }
    override fun start() {
        super.start()
        /*
     * Call super.start() to call the base class start animation method.
     * Then add a handler to call onAnimationFinish() when the total
     * duration for the animation has passed
     */
        mAnimationHandler = Handler()
        mAnimationHandler.post(object:Runnable {
            public override fun run() {
                onAnimationStart()
            }
        })
        mAnimationHandler.postDelayed(object:Runnable {
            public override fun run() {
                onAnimationFinish()
            }
        }, totalDuration.toLong())
    }
    /**
     * Called when the animation finishes.
     */
    abstract fun onAnimationFinish()
    /**
     * Called when the animation starts.
     */
    abstract fun onAnimationStart()
}