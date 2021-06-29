package com.eee.www.nugasa

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.eee.www.nugasa.databinding.ActivityMainBinding
import com.eee.www.nugasa.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val viewModel: MainActivityViewModel by viewModels()

    object Constants {
        const val MENU_PICK = 0
        const val MENU_TEAM = 1
        const val MENU_RANK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindData()

        initCanvasView()
        initMenuSpinner()
        initPickCountSpinner()
        initTeamCountSpinner()
        initObservers()
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
                    val alphaAnim = (if (fingerPressed)
                        ObjectAnimator.ofFloat(this, AppCompatSpinner.ALPHA, 1f, 0f)
                        else ObjectAnimator.ofFloat(this, AppCompatSpinner.ALPHA, 0f, 1f))

                    val transAnim = (if (fingerPressed)
                        ObjectAnimator.ofFloat(this, AppCompatSpinner.TRANSLATION_Y, -100f)
                        else ObjectAnimator.ofFloat(this, AppCompatSpinner.TRANSLATION_Y, 100f))

                    transAnim.interpolator = AccelerateInterpolator(3f)

                    val animatorSet = AnimatorSet()
                    animatorSet.playTogether(alphaAnim, transAnim)
                    animatorSet.duration = 500
                    animatorSet.start()
                }

                menuSpinner.isEnabled = !fingerPressed
                pickCountSpinner.isEnabled = !fingerPressed
                teamCountSpinner.isEnabled = !fingerPressed
                // appears only once
                if (fingerPressed)
                    helpTextView.visibility = View.GONE
            })
    }

    private fun initMenuSpinner() {
        menuSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setMenuPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initPickCountSpinner() {
        pickCountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = pickCountSpinner.getItemAtPosition(position) as Int
                viewModel.setPickCount(item)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initTeamCountSpinner() {
        teamCountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = teamCountSpinner.getItemAtPosition(position) as Int
                viewModel.setTeamCount(item)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun initObservers() {
        val fingerCountObserver = Observer<Int> { count -> canvasView.fingerCount = count }
        val modeObserver = Observer<Int> { position ->
            when (position) {
                Constants.MENU_PICK -> {
                    pickCountSpinner.show()
                    teamCountSpinner.gone()
                    viewModel.pickCount.observe(this, fingerCountObserver)
                    viewModel.teamCount.removeObservers(this)
                }
                Constants.MENU_TEAM -> {
                    pickCountSpinner.gone()
                    teamCountSpinner.show()
                    viewModel.pickCount.removeObservers(this)
                    viewModel.teamCount.observe(this, fingerCountObserver)
                }
                Constants.MENU_RANK -> {
                    pickCountSpinner.hide()
                    teamCountSpinner.hide()
                    viewModel.pickCount.removeObservers(this)
                    viewModel.teamCount.removeObservers(this)
                }
            }
            canvasView.mode = position
        }
        viewModel.menuPosition.observe(this, modeObserver)
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