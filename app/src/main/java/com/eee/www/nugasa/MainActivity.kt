package com.eee.www.nugasa

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.eee.www.nugasa.databinding.ActivityMainBinding
import com.eee.www.nugasa.ui.IntroActivity
import com.eee.www.nugasa.ui.Mediator
import com.eee.www.nugasa.utils.PickFingerPicker
import com.eee.www.nugasa.utils.RankFingerPicker
import com.eee.www.nugasa.utils.TeamFingerPicker
import com.eee.www.nugasa.utils.setFullScreen
import com.eee.www.nugasa.viewmodels.MainActivityViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

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
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (!viewModel.getStartedOnce()) {
            showIntroActivity()
            viewModel.setStartedOnce()
        }

        bindData()
        initMediator()
        initOnDataChanged()

        binding.licenseButton.setOnClickListener {
            val intent = Intent(this, OssLicensesMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showIntroActivity() {
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
    }

    private fun bindData() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initMediator() {
        binding.menuSpinner.mediator = this
        binding.pickCountSpinner.mediator = this
        binding.teamCountSpinner.mediator = this
        binding.canvasView.mediator = this
    }

    override fun initOnDataChanged() {
        val modeObserver = Observer<Int> { position ->
            when (position) {
                Constants.MENU_PICK -> {
                    binding.canvasView.fingerPicker = PickFingerPicker(this, binding.canvasView.fingerMap)
                    binding.pickCountSpinner.show()
                    binding.teamCountSpinner.gone()
                }
                Constants.MENU_TEAM -> {
                    binding.canvasView.fingerPicker = TeamFingerPicker(this, binding.canvasView.fingerMap)
                    binding.pickCountSpinner.gone()
                    binding.teamCountSpinner.show()
                }
                Constants.MENU_RANK -> {
                    binding.canvasView.fingerPicker = RankFingerPicker(this, binding.canvasView.fingerMap)
                    binding.pickCountSpinner.hide()
                    binding.teamCountSpinner.hide()
                }
            }
        }
        viewModel.menuPosition.observe(this, modeObserver)

        val fingerCountObserver = Observer<Int> { count -> binding.canvasView.fingerCount = count }
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
        binding.konfettiViewLeft.start(viewModel.partyConfettiConfigLeft)
        binding.konfettiViewRight.start(viewModel.partyConfettiConfigRight)
    }

    override fun onDestroy() {
        binding.canvasView.destroy()
        super.onDestroy()
    }
}