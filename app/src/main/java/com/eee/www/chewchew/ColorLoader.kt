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
            mColorList.add(context.resources.getColor(R.color.medium_turquoise))
            mColorList.add(context.resources.getColor(R.color.sugar_cane))
            mColorList.add( context.resources.getColor(R.color.tickle_me_pink))
            mColorList.add(context.resources.getColor(R.color.tangerine_orange))
            mColorList.add(context.resources.getColor(R.color.golden_yellow))

            mColorList.add(context.resources.getColor(R.color.tangerine))
            mColorList.add(context.resources.getColor(R.color.wild_watermelon))
            mColorList.add(context.resources.getColor(R.color.apple_green))
            mColorList.add(context.resources.getColor(R.color.light_pink))
            mColorList.add(context.resources.getColor(R.color.electric_lime))
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