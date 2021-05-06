package com.eee.www.chewchew

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker

class FingerCountPicker : NumberPicker {
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
}