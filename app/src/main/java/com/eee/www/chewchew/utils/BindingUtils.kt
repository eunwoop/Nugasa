package com.eee.www.chewchew.utils

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
