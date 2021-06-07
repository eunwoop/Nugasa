package com.eee.www.chewchew

import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.ArrayAdapter
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
        initMenuSpinner()
    }

    private fun initCanvasView() {
        canvasView.fingerPressed.observe(
            this,
            Observer { fingerPressed ->
                menuLayout.apply {
                   (if (fingerPressed)
                        ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                            duration = 500;
                        }
                    else
                        ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                            duration = 500;
                        }).start()
                    this.isEnabled = !fingerPressed
                }
            })
        viewModel.fingerCount.observe(
            this,
            Observer { fingerCount -> canvasView.fingerCount = fingerCount })
    }

    private fun initCountPicker() {
        countPicker.value.apply { viewModel.fingerCount.value }
        countPicker.setOnValueChangedListener { _, _, newVal ->
            viewModel.setFingerSelectionCount(
                newVal
            )
        }
    }

    private fun initMenuSpinner() {
        ArrayAdapter.createFromResource(this, R.array.menu_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            menuSpinner.adapter = adapter
        }
    }

    override fun onDestroy() {
        canvasView.destroy()
        super.onDestroy()
    }
}