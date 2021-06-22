package com.eee.www.chewchew.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.eee.www.chewchew.viewmodels.PreferenceViewModel.FingerPreference.MENU_POSITION_KEY
import com.eee.www.chewchew.viewmodels.PreferenceViewModel.FingerPreference.PICK_COUNT_KEY
import com.eee.www.chewchew.viewmodels.PreferenceViewModel.FingerPreference.TEAM_COUNT_KEY

class PreferenceViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    object FingerPreference {
        const val MENU_POSITION_KEY = "menuPosition"
        const val PICK_COUNT_KEY = "pickCount"
        const val TEAM_COUNT_KEY = "teamCount"
    }

    val menuPosition
        get() = savedStateHandle.getLiveData(MENU_POSITION_KEY, 0)
    val pickCount
        get() = savedStateHandle.getLiveData(PICK_COUNT_KEY, 1)
    val teamCount
        get() = savedStateHandle.getLiveData(TEAM_COUNT_KEY, 2)

    fun setMenuPosition(position: Int) {
        savedStateHandle.set(MENU_POSITION_KEY, position)
    }

    fun setPickCount(position: Int) {
        savedStateHandle.set(PICK_COUNT_KEY, position)
    }

    fun setTeamCount(position: Int) {
        savedStateHandle.set(TEAM_COUNT_KEY, position)
    }
}