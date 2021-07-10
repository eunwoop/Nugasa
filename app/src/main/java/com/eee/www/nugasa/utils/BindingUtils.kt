package com.eee.www.nugasa.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter

@BindingAdapter("position", requireAll = true)
fun setSelection(view: AppCompatSpinner, position: Int) {
    view.setSelection(position)
}

@BindingAdapter("item", requireAll = true)
fun setSelectionItem(view: AppCompatSpinner, item: Any) {
    var position = 0
    if (item is String) {
        val arrayAdapter = view.adapter as ArrayAdapter<String>
        position = arrayAdapter.getPosition(item)
    } else if (item is Int) {
        val arrayAdapter = view.adapter as ArrayAdapter<Int>
        position = arrayAdapter.getPosition(item)
    }
    view.setSelection(position)
}

@BindingAdapter("pressed", requireAll = true)
fun setPressed(view: View, pressed: Boolean) {
    view.apply {
        val alphaAnim = (if (pressed)
            ObjectAnimator.ofFloat(this, AppCompatSpinner.ALPHA, 1f, 0f)
        else ObjectAnimator.ofFloat(this, AppCompatSpinner.ALPHA, 0f, 1f))

        val transAnim = (if (pressed)
            ObjectAnimator.ofFloat(this, AppCompatSpinner.TRANSLATION_Y, -100f)
        else ObjectAnimator.ofFloat(this, AppCompatSpinner.TRANSLATION_Y, 100f))

        transAnim.interpolator = AccelerateInterpolator(3f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(alphaAnim, transAnim)
        animatorSet.duration = 500
        animatorSet.start()
    }
}
