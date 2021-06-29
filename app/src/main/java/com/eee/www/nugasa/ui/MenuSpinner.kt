package com.eee.www.nugasa.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import com.eee.www.nugasa.R

class MenuSpinner : RoundedSpinner {

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
        background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_popup_menu_bg, null)
        setPopupBackgroundResource(R.drawable.spinner_popup_menu_bg)
    }

    override fun setAttributes(attrs: AttributeSet) {
        ArrayAdapter.createFromResource(context, R.array.menu_array, R.layout.spinner_menu_item).apply {
            setDropDownViewResource(R.layout.spinner_menu_item)
            adapter = this
        }
    }
}