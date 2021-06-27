package com.eee.www.nugasa.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.DEFAULT_MENU_POSITION
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.DEFAULT_PICK_COUNT
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.DEFAULT_TEAM_COUNT
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.MENU_POSITION_KEY
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.MENU_POSITION_PICK
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.MENU_POSITION_TEAM
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.PICK_COUNT_KEY
import com.eee.www.nugasa.viewmodels.MainActivityViewModel.Constants.TEAM_COUNT_KEY

class MainActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
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
        get() = switchMap(menuPosition) {
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