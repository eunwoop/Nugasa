package com.eee.www.chewchew.utils

import android.content.Context
import android.util.TypedValue

object ViewUtils {
    fun dpToPx(context: Context?, dp: Float): Float {
        if (context != null) {
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp, context.resources.displayMetrics
            )
        }
        return dp
    }
}