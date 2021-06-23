package com.eee.www.chewchew.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import com.eee.www.chewchew.R

class MenuSpinner : RoundedSpinner {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setAttributes(attrs: AttributeSet) {
        ArrayAdapter.createFromResource(context, R.array.menu_array, R.layout.spinner_item).apply {
            setDropDownViewResource(R.layout.spinner_item)
            adapter = this
        }
    }
}