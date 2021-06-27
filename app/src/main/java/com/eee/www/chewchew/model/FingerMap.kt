package com.eee.www.chewchew.model

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import com.eee.www.chewchew.utils.TAG

class FingerMap {
    companion object {
        private const val MAX_SIZE = 10
    }

    private val _map = mutableMapOf<Int, PointF>()
    val map
        get() = _map as Map<Int, PointF>
    val size
        get() = _map.size

    fun add(event: MotionEvent): Int {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        _map[pointerId] = PointF(event.getX(pointerIndex), event.getY(pointerIndex))
        return pointerId
    }

    fun move(event: MotionEvent) {
        for (pointerIndex in 0 until event.pointerCount) {
            val pointerId = event.getPointerId(pointerIndex)
            _map[pointerId]?.apply {
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)
            }
        }
    }

    fun remove(event: MotionEvent): Int {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        _map.remove(pointerId)
        return pointerId
    }

    fun isEmpty() = _map.isEmpty()

    fun isFull() = size == MAX_SIZE

    fun select(n: Int): List<Int> {
        val selected = shuffleList()
        for (i in 0 until (_map.size - n)) {
            selected.removeAt(0)
        }
        return selected
    }

    fun selectTeam(n: Int): Map<Int, Int> {
        var teamId: Int
        val numOfOneTeam = _map.size / n

        val tempList = shuffleList()
        val teamMap = mutableMapOf<Int, Int>()
        for (i in 0 until tempList.size) {
            teamId = i / numOfOneTeam
            teamMap[tempList[i]] = if (teamId < n) teamId else -1
        }
        teamId = 0
        teamMap.forEach {
            if (teamMap[it.key] == -1) {
                teamMap[it.key] = teamId++
            }
        }
        return teamMap
    }

    fun selectRank(): Map<Int, Int> {
        val tempList = shuffleList()
        val rankMap = mutableMapOf<Int, Int>()
        _map.forEach {
            rankMap[it.key] = tempList[it.key] + 1 // +1 is for except 0
        }
        return rankMap
    }

    private fun shuffleList(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (pointerId in _map.keys) {
            list.add(pointerId)
        }
        list.shuffle()
        return list
    }

    fun print() {
        _map.forEach { point ->
            Log.d(TAG, "touchPoint:(${point.value.x},${point.value.y})")
        }
    }
}