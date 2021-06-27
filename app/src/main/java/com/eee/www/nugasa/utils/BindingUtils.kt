package com.eee.www.nugasa.utils

import android.animation.ObjectAnimator
import android.view.View
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
        (if (pressed)
            ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                duration = 500
            }
        else
            ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                duration = 500
            }).start()
    }
    view.isEnabled = !pressed
}
