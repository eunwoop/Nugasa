package com.eee.www.nugasa.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.eee.www.nugasa.R

class MenuSpinner : RoundedSpinner, MediatedView {

    override var mediator: Mediator? = null

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

    override fun initOnItemSelectedListener() {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mediator?.setMode(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}