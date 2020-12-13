package com.safesoft.safemobile.ui.generics

import android.view.View
import android.view.animation.AnimationUtils
import com.safesoft.safemobile.R

interface BaseAnimations {

    fun editTextErrorAnimation(duration: Long = 300, vararg views: View) {
        val animation = AnimationUtils.loadAnimation(
            views[0].context,
            R.anim.shake,
        )
        animation.duration = duration
        views.forEach {
            it.startAnimation(animation)
        }
    }


}