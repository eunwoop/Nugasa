package com.eee.www.nugasa.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner

abstract class RoundedSpinner : AppCompatSpinner {

    constructor(context: Context) : super(context) {
        initOnItemSelectedListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        attrs?.also { setAttributes(it) }
        initOnItemSelectedListener()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.also { setAttributes(it) }
        initOnItemSelectedListener()
    }

    protected abstract fun setAttributes(attrs: AttributeSet)

    protected abstract fun initOnItemSelectedListener()
}