package com.eee.www.chewchew.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.eee.www.chewchew.R

class RoundedSpinner : AppCompatSpinner {

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
        attrs?.let {
            val arrayAdapter = ArrayAdapter<Int>(context, R.layout.spinner_item)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
            adapter = arrayAdapter
        }
    }
}