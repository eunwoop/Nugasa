package com.eee.www.nugasa.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.eee.www.nugasa.MainActivity
import com.eee.www.nugasa.data.getBooleanSharedPref
import com.eee.www.nugasa.data.setBooleanSharedPref
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.DEFAULT_MENU_POSITION
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.DEFAULT_PICK_COUNT
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.DEFAULT_TEAM_COUNT
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.MENU_POSITION_KEY
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.MENU_POSITION_PICK
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.MENU_POSITION_TEAM
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.PICK_COUNT_KEY
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.TEAM_COUNT_KEY
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter

class MainActivityViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    object Constants {
        const val MENU_POSITION_KEY = "menuPosition"
        const val PICK_COUNT_KEY = "pickCount"
        const val TEAM_COUNT_KEY = "teamCount"

        const val MENU_POSITION_PICK = 0
        const val MENU_POSITION_TEAM = 1
        const val MENU_POSITION_RANK = 2

        const val DEFAULT_MENU_POSITION = MENU_POSITION_PICK
        const val DEFAULT_PICK_COUNT = 1
        const val DEFAULT_TEAM_COUNT = 2
    }

    val menuPosition: LiveData<Int>
        get() = savedStateHandle.getLiveData(MENU_POSITION_KEY, DEFAULT_MENU_POSITION)
    val fingerCount: LiveData<Int>
        get() = menuPosition.switchMap {
            when (it) {
                MENU_POSITION_PICK -> pickCount
                MENU_POSITION_TEAM -> teamCount
                else -> MutableLiveData(1)
            }
        }
    val pickCount: LiveData<Int>
        get() = savedStateHandle.getLiveData(PICK_COUNT_KEY, DEFAULT_PICK_COUNT)
    val teamCount: LiveData<Int>
        get() = savedStateHandle.getLiveData(TEAM_COUNT_KEY, DEFAULT_TEAM_COUNT)
    var fingerPressed = MutableLiveData(false)

    val partyConfettiConfigLeft: Party = Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 360,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 100).max(100),
        position = Position.Relative(0.1, 0.5)
    )
    val partyConfettiConfigRight: Party = Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 360,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 100).max(100),
        position = Position.Relative(0.9, 0.5)
    )

    fun setMenuPosition(position: Int) {
        savedStateHandle.set(MENU_POSITION_KEY, position)
    }

    fun setPickCount(position: Int) {
        savedStateHandle.set(PICK_COUNT_KEY, position)
    }

    fun setTeamCount(position: Int) {
        savedStateHandle.set(TEAM_COUNT_KEY, position)
    }

    fun setFingerPressed(pressed: Boolean) {
        fingerPressed.value = pressed
    }

    fun getStartedOnce() : Boolean {
        return getBooleanSharedPref(getApplication(), MainActivity.PREF_NAME, MainActivity.PREF_KEY)
    }

    fun setStartedOnce() {
        setBooleanSharedPref(getApplication(), MainActivity.PREF_NAME, MainActivity.PREF_KEY, true)
    }
}