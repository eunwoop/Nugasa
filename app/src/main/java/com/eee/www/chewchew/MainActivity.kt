package com.eee.www.chewchew

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
        setContentView(R.layout.activity_main)
        initCanvasView()
        initCountPicker(viewModel.pickList)
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
                }
                menuSpinner.isEnabled = !fingerPressed
                countSpinner.isEnabled = !fingerPressed
                // appears only once
                if (fingerPressed)
                    helpTextView.visibility = View.GONE
            })
        viewModel.fingerCount.observe(
            this,
            Observer { fingerCount -> canvasView.fingerCount = fingerCount })
    }

    private fun initMenuSpinner() {
        ArrayAdapter.createFromResource(this, R.array.menu_array, R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item)
            menuSpinner.adapter = adapter
        }

        menuSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {
                    Constants.MENU_PICK-> {
                        countSpinner.isEnabled = true
                        countSpinner.visibility = View.VISIBLE
                        initCountPicker(viewModel.pickList)
                    }
                    Constants.MENU_TEAM-> {
                        countSpinner.isEnabled = true
                        countSpinner.visibility = View.VISIBLE
                        initCountPicker(viewModel.teamList)
                    }
                    Constants.MENU_RANK-> {
                        countSpinner.isEnabled = false
                        countSpinner.visibility = View.GONE
                    }
                }
                canvasView.mode = position
                viewModel.setFingerSelectionCount(viewModel.pickList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun initCountPicker(countList: List<Int>) {
        val arrayAdapter = ArrayAdapter<Int>(this, R.layout.spinner_item, countList)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        countSpinner.adapter = arrayAdapter

        // TODO: set initial value of menu & count
//        if (viewModel.fingerCount.value != null) viewModel.fingerCount.value else 0.let {
//            countSpinner.setSelection(countList[it])
//        }

        countSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) setFullScreen()
    }

    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars())
                controller.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    override fun onDestroy() {
        canvasView.destroy()
        super.onDestroy()
    }
}