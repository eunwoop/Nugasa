package com.eee.www.nugasa.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.animation.doOnEnd
import com.eee.www.nugasa.R

abstract class CountSpinner : RoundedSpinner, MediatedView {

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

class PickCountSpinner : CountSpinner {

    override var mediator: Mediator? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun initOnItemSelectedListener() {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = getItemAtPosition(position) as Int
                mediator?.setPickCount(item)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}

class TeamCountSpinner : CountSpinner {

    override var mediator: Mediator? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun initOnItemSelectedListener() {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = getItemAtPosition(position) as Int
                mediator?.setTeamCount(item)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}