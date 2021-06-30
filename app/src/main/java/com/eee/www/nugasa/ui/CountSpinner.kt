package com.eee.www.nugasa.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import com.eee.www.nugasa.R

class CountSpinner : RoundedSpinner {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setAttributes(attrs: AttributeSet) {
        val min = attrs.getAttributeIntValue(null, "min", 0)
        val max = attrs.getAttributeIntValue(null, "max", 0)
        if (min in 1 until max) {
            ArrayAdapter<Int>(context, R.layout.spinner_num_item, R.id.textView, (min..max).toList()).apply {
                setDropDownViewResource(R.layout.spinner_num_item)
                adapter = this
            }
        }
    }
}