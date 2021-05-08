package com.eee.www.chewchew

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.eee.www.chewchew.viewmodels.PreferenceViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel: PreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCanvasView()
        initCountPicker()
    }

    private fun initCanvasView() {
        canvasView.fingerPressed.observe(
            this,
            Observer { fingerPressed ->
                countPicker.apply {
                    if (fingerPressed) {
                        fadeOut()
                    } else {
                        fadeIn()
                    }
                }
            })
        viewModel.fingerCount.observe(
            this,
            Observer { fingerCount -> canvasView.fingerCount = fingerCount })
    }

    private fun initCountPicker() {
        countPicker.value = viewModel.fingerCount.value!!
        countPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.setFingerSelectionCount(
                newVal
            )
        }
    }
}