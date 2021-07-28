package com.eee.www.nugasa.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.animation.doOnEnd

object AnimationUtils {

    fun show(v: View) {
        v.isEnabled = true
        v.visibility = AppCompatSpinner.VISIBLE

        val alphaAnim = ObjectAnimator.ofFloat(v, AppCompatSpinner.ALPHA, 0f, 1f)
        val transAnim = ObjectAnimator.ofFloat(v, AppCompatSpinner.TRANSLATION_X,  30f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnim, transAnim)
        animatorSet.duration = 500

        animatorSet.start()
    }

    fun hide(v: View) {
        v.isEnabled = false

        val alphaAnim = ObjectAnimator.ofFloat(v, AppCompatSpinner.ALPHA, 1f, 0f)
        alphaAnim.doOnEnd {
            v.visibility = AppCompatSpinner.INVISIBLE
        }
        val transAnim = ObjectAnimator.ofFloat(v, AppCompatSpinner.TRANSLATION_X, -30f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnim, transAnim)
        animatorSet.duration = 500

        animatorSet.start()
    }

    fun gone(v: View) {
        v.isEnabled = false
        v.visibility = AppCompatSpinner.GONE

        // this is for show() animation working from -30f
        val transAnim = ObjectAnimator.ofFloat(v, AppCompatSpinner.TRANSLATION_X, -30f)
        transAnim.duration = 500
        transAnim.start()
    }
}