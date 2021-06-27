package com.eee.www.nugasa.utils

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.eee.www.nugasa.R

object FingerColors {

    private const val MAX_SIZE = 10

    private val colorList = arrayListOf<Int>()

    fun shuffle(context: Context) {
        if (colorList.isEmpty()) {
            loadColor(context)
        }
        colorList.shuffle()
    }

    private fun loadColor(context: Context) {
        with(colorList) {
            add(ResourcesCompat.getColor(context.resources, R.color.medium_turquoise, null))
            add(ResourcesCompat.getColor(context.resources, R.color.sugar_cane, null))
            add(ResourcesCompat.getColor(context.resources, R.color.carnation_pink, null))
            add(ResourcesCompat.getColor(context.resources, R.color.selective_yellow, null))
            add(ResourcesCompat.getColor(context.resources, R.color.golden_yellow, null))
            add(ResourcesCompat.getColor(context.resources, R.color.purple, null))
            add(ResourcesCompat.getColor(context.resources, R.color.wild_watermelon, null))
            add(ResourcesCompat.getColor(context.resources, R.color.apple_green, null))
            add(ResourcesCompat.getColor(context.resources, R.color.light_pink, null))
            add(ResourcesCompat.getColor(context.resources, R.color.electric_lime, null))
        }
    }

    fun randomColor(randomInt: Int) = colorList[randomInt % MAX_SIZE]
}