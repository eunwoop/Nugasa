package com.eee.www.nugasa

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.eee.www.nugasa.databinding.ActivityMainBinding
import com.eee.www.nugasa.ui.Mediator
import com.eee.www.nugasa.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Mediator {
    object Constants {
        const val MENU_PICK = 0
        const val MENU_TEAM = 1
        const val MENU_RANK = 2
    }

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindData()
        initCanvasView()
        initMediator()
        initOnDataChanged()
    }

    private fun bindData() {
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
    }

    private fun initCanvasView() {
        canvasView.fingerPressed.observe(
            this,
            Observer { fingerPressed ->
                menuLayout.apply {
                    (if (fingerPressed)
                        ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                            duration = 500
                        }
                    else
                        ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                            duration = 500
                        }).start()
                }
                menuSpinner.isEnabled = !fingerPressed
                pickCountSpinner.isEnabled = !fingerPressed
                teamCountSpinner.isEnabled = !fingerPressed
                // appears only once
                if (fingerPressed)
                    helpTextView.visibility = View.GONE
            })
    }

    private fun initMediator() {
        menuSpinner.mediator = this
        pickCountSpinner.mediator = this
        teamCountSpinner.mediator = this
    }

    override fun initOnDataChanged() {
        val modeObserver = Observer<Int> { position ->
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
        viewModel.menuPosition.observe(this, modeObserver)

        val fingerCountObserver = Observer<Int> { count -> canvasView.fingerCount = count }
        viewModel.fingerCount.observe(this, fingerCountObserver)
    }

    override fun setMode(mode: Int) {
        viewModel.setMenuPosition(mode)
    }

    override fun setPickCount(count: Int) {
        viewModel.setPickCount(count)
    }

    override fun setTeamCount(count: Int) {
        viewModel.setTeamCount(count)
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