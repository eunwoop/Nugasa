package com.eee.www.chewchew.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.res.ResourcesCompat
import com.eee.www.chewchew.R

class RoundedSpinner : AppCompatSpinner {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
        setPopupBackgroundResource(R.drawable.spinner_popup_bg)
    }
}