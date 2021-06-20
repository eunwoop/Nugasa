package com.eee.www.chewchew.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class PreferenceViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    object FingerPreference {
        const val SELECTION_COUNT_KEY = "fingerSelectionCount"
    }

    val fingerCount = getFingerSelectionCount()
    val teamList = listOf(2,3,4,5)
    val pickList = listOf(1,2,3,4,5)

    fun setFingerSelectionCount(count: Int) {
        savedStateHandle.set(FingerPreference.SELECTION_COUNT_KEY, count)
    }

    private fun getFingerSelectionCount(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(FingerPreference.SELECTION_COUNT_KEY, 1)
    }
}