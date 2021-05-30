package com.eee.www.chewchew.utils

import android.content.Context
import com.eee.www.chewchew.R

object FingerColors {

    private val colorList = arrayListOf<Int>()

    fun shuffle(context: Context) : List<Int> {
        if (colorList.isEmpty()) {
            loadColor(context)
        }
        colorList.shuffle()
        return colorList
    }

    private fun loadColor(context: Context) {
        with(colorList) {
            add(context.resources.getColor(R.color.medium_turquoise))
            add(context.resources.getColor(R.color.sugar_cane))
            add(context.resources.getColor(R.color.tickle_me_pink))
            add(context.resources.getColor(R.color.tangerine_orange))
            add(context.resources.getColor(R.color.golden_yellow))
            add(context.resources.getColor(R.color.tangerine))
            add(context.resources.getColor(R.color.wild_watermelon))
            add(context.resources.getColor(R.color.apple_green))
            add(context.resources.getColor(R.color.light_pink))
            add(context.resources.getColor(R.color.electric_lime))
        }
    }
}