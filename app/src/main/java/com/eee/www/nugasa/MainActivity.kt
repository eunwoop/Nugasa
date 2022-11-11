package com.eee.www.nugasa

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.eee.www.nugasa.databinding.ActivityMainBinding
import com.eee.www.nugasa.ui.IntroActivity
import com.eee.www.nugasa.ui.LicenseActivity
import com.eee.www.nugasa.ui.Mediator
import com.eee.www.nugasa.utils.PickFingerPicker
import com.eee.www.nugasa.utils.RankFingerPicker
import com.eee.www.nugasa.utils.TeamFingerPicker
import com.eee.www.nugasa.utils.setFullScreen
import com.eee.www.nugasa.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Mediator {
    object Constants {
        const val MENU_PICK = 0
        const val MENU_TEAM = 1
        const val MENU_RANK = 2
    }

    companion object {
        const val PREF_NAME = "DEFAULT_SHARED_PREF"
        const val PREF_KEY = "IS_STARTED_BEFORE"
    }

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!viewModel.getStartedOnce()) {
            showIntroActivity()
            viewModel.setStartedOnce()
        }

        bindData()
        initMediator()
        initOnDataChanged()

        licenseButton.setOnClickListener {
            val intent = Intent(this, LicenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showIntroActivity() {
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
    }

    private fun bindData() {
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initMediator() {
        menuSpinner.mediator = this
        pickCountSpinner.mediator = this
        teamCountSpinner.mediator = this
        canvasView.mediator = this
    }

    override fun initOnDataChanged() {
        val modeObserver = Observer<Int> { position ->
            when (position) {
                Constants.MENU_PICK -> {
                    canvasView.fingerPicker = PickFingerPicker(this, canvasView.fingerMap)
                    pickCountSpinner.show()
                    teamCountSpinner.gone()
                }
                Constants.MENU_TEAM -> {
                    canvasView.fingerPicker = TeamFingerPicker(this, canvasView.fingerMap)
                    pickCountSpinner.gone()
                    teamCountSpinner.show()
                }
                Constants.MENU_RANK -> {
                    canvasView.fingerPicker = RankFingerPicker(this, canvasView.fingerMap)
                    pickCountSpinner.hide()
                    teamCountSpinner.hide()
                }
            }
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

    override fun setPressed(pressed: Boolean) {
        viewModel.setFingerPressed(pressed)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) setFullScreen(window)
    }

    override fun showPartyConfetti() {
        konfettiViewLeft.start(viewModel.partyConfettiConfigLeft)
        konfettiViewRight.start(viewModel.partyConfettiConfigRight)
    }

    override fun onDestroy() {
        canvasView.destroy()
        super.onDestroy()
    }
}