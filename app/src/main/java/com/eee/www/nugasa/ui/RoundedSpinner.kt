package com.eee.www.nugasa.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.res.ResourcesCompat
import com.eee.www.nugasa.R

abstract class RoundedSpinner : AppCompatSpinner {

    constructor(context: Context) : super(context) {
        initView()
        initOnItemSelectedListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        attrs?.also { setAttributes(it) }
        initOnItemSelectedListener()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
        attrs?.also { setAttributes(it) }
        initOnItemSelectedListener()
    }

    private fun initView() {
        background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
        setPopupBackgroundResource(R.drawable.spinner_popup_bg)
    }

    protected abstract fun setAttributes(attrs: AttributeSet)

    protected abstract fun initOnItemSelectedListener()

    fun show() {
        isEnabled = true
        visibility = VISIBLE
    }

    fun hide() {
        isEnabled = false
        visibility = GONE
    }
}