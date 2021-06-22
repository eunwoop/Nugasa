package com.eee.www.chewchew

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.eee.www.chewchew.databinding.ActivityMainBinding
import com.eee.www.chewchew.viewmodels.PreferenceViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val viewModel: PreferenceViewModel by viewModels()

    object Constants {
        const val MENU_PICK = 0
        const val MENU_TEAM = 1
        const val MENU_RANK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        initCanvasView()
        initMenuSpinner(binding)
        initCountPicker(viewModel.pickList)
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

    private fun initMenuSpinner(binding: ActivityMainBinding) {
        binding.menuArrayResId = R.array.menu_array
        menuSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        Constants.MENU_PICK -> {
                            pickCountSpinner.show()
                            teamCountSpinner.hide()
                        }
                        Constants.MENU_TEAM -> {
                            pickCountSpinner.hide()
                            teamCountSpinner.show()
                        }
                        Constants.MENU_RANK -> {
                            pickCountSpinner.hide()
                            teamCountSpinner.hide()
                        }
                    }
                    canvasView.mode = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun initCountPicker(countList: List<Int>) {
        pickCountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setFingerSelectionCount(countList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun onDestroy() {
        canvasView.destroy()
        super.onDestroy()
    }
}