package com.eee.www.nugasa.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.animation.doOnEnd


abstract class RoundedSpinner : AppCompatSpinner {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.also { setAttributes(it) }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.also { setAttributes(it) }
    }

    protected abstract fun setAttributes(attrs: AttributeSet)

    fun show() {
        isEnabled = true
        visibility = VISIBLE

        val alphaAnim = ObjectAnimator.ofFloat(this, ALPHA, 0f, 1f)
        val transAnim = ObjectAnimator.ofFloat(this, TRANSLATION_X,  30f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnim, transAnim)
        animatorSet.duration = 500

        animatorSet.start()
    }

    fun hide() {
        isEnabled = false

        val alphaAnim = ObjectAnimator.ofFloat(this, ALPHA, 1f, 0f)
        alphaAnim.doOnEnd {
            visibility = GONE
        }
        val transAnim = ObjectAnimator.ofFloat(this, TRANSLATION_X, -30f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnim, transAnim)
        animatorSet.duration = 500

        animatorSet.start()
    }

    fun gone() {
        isEnabled = false
        visibility = GONE
    }
}