package com.eee.www.chewchew.utils

import android.widget.ArrayAdapter
import androidx.annotation.ArrayRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import com.eee.www.chewchew.R

@BindingAdapter("arrayResId", requireAll = true)
fun setArrayAdapter(view: AppCompatSpinner, @ArrayRes arrayResId: Int) {
    ArrayAdapter.createFromResource(
        view.context, arrayResId, R.layout.spinner_item
    ).apply {
        setDropDownViewResource(R.layout.spinner_item)
        view.adapter = this
    }
}
