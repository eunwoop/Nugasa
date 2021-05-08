package com.eee.www.chewchew

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker

class FingerCountPicker : NumberPicker {

    private val animationTime = resources.getInteger(R.integer.pickerAnimTime).toLong()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttributeSet(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributeSet(attrs)
    }

    private fun setAttributeSet(attrs: AttributeSet?) {
        attrs?.run {
            minValue = getAttributeIntValue(null, "min", 0)
            maxValue = getAttributeIntValue(null, "max", 0)
        }
    }

    fun fadeIn() {
        isEnabled = true

        alpha = 0f
        visibility = View.VISIBLE
        animate().alpha(1f)
            .setDuration(animationTime)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.VISIBLE
                }
            })
    }

    fun fadeOut() {
        isEnabled = false

        animate().alpha(0f)
            .setDuration(animationTime)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                }
            })
    }
}