package com.eee.www.chewchew

import android.content.Context

class ColorLoader private constructor(context: Context) {
    companion object {
        var mColorList = arrayListOf<Int>()
        private var sColorLoader: ColorLoader? = null

        fun getInstance(context:Context) : ColorLoader = sColorLoader ?: synchronized(this) {
            sColorLoader ?: ColorLoader(context).also {
                sColorLoader = it
            }
        }

        private fun loadColor(context: Context) {
            with(mColorList) {
                add(context.resources.getColor(R.color.medium_turquoise))
                add(context.resources.getColor(R.color.sugar_cane))
                add( context.resources.getColor(R.color.tickle_me_pink))
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

    init {
        loadColor(context)
    }

    fun getColorList() : List<Int> {
        mColorList.shuffle()
        return mColorList
    }
}