package com.eee.www.chewchew.ui

import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

object CenterSnackbar {

    fun showShort(view: View, stringId: Int) {
        show(view, stringId, Snackbar.LENGTH_SHORT)
    }

    fun showLong(view: View, stringId: Int) {
        show(view, stringId, Snackbar.LENGTH_LONG)
    }

    private fun show(view: View, stringId: Int, period: Int) {
        val snackbar = Snackbar.make(view, stringId, period)
        val textView =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackbar.show()
    }
}