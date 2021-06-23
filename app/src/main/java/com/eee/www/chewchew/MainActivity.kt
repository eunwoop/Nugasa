package com.eee.www.chewchew

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
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
                    teamCountSpinner.hide()
                    viewModel.pickCount.observe(this, fingerCountObserver)
                    viewModel.teamCount.removeObservers(this)
                }
                Constants.MENU_TEAM -> {
                    pickCountSpinner.hide()
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

    override fun onDestroy() {
        canvasView.destroy()
        super.onDestroy()
    }
}