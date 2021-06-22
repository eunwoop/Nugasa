package com.eee.www.chewchew.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.res.ResourcesCompat
import com.eee.www.chewchew.R

class RoundedSpinner : AppCompatSpinner {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        attrs?.also { setAttributes(it) }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
        attrs?.also { setAttributes(it) }
    }

    private fun initView() {
        background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
        setPopupBackgroundResource(R.drawable.spinner_popup_bg)
    }

    private fun setAttributes(attrs: AttributeSet) {
        val min = attrs.getAttributeIntValue(null, "min", 0)
        val max = attrs.getAttributeIntValue(null, "max", 0)
        if (min in 1 until max) {
            ArrayAdapter<Int>(context, R.layout.spinner_item, (min..max).toList()).apply {
                setDropDownViewResource(R.layout.spinner_item)
                adapter = this
            }
        }
    }

    fun show() {
        isEnabled = true
        visibility = VISIBLE
    }

    fun hide() {
        isEnabled = false
        visibility = GONE
    }

    fun setSelectionItem(item: Any) {
        var position = 0
        if (item is String) {
            val arrayAdapter = adapter as ArrayAdapter<String>
            position = arrayAdapter.getPosition(item)
        } else if (item is Int) {
            val arrayAdapter = adapter as ArrayAdapter<Int>
            position = arrayAdapter.getPosition(item)
        }
        setSelection(position)
    }
}