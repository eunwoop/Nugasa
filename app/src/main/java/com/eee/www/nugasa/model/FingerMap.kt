package com.eee.www.nugasa.model

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import com.eee.www.nugasa.utils.TAG

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
        val selected = mutableListOf<Int>()
        for (pointerId in _map.keys) {
            selected.add(pointerId)
        }
        selected.shuffle()
        for (i in 0 until (_map.size - n)) {
            selected.removeAt(0)
        }
        return selected
    }

    fun selectTeam(n: Int): MutableMap<Int, Int> {
        val tempList = mutableListOf<Int>() // to shuffle
        for (key in _map.keys) {
            tempList.add(key)
        }
        tempList.shuffle()

        var teamId: Int;
        val numOfOneTeam = _map.size / n;

        val teamMap = mutableMapOf<Int, Int>()
        for (i in 0 until tempList.size) {
            teamId = i / numOfOneTeam
            teamMap.put(tempList[i], if (teamId < n) teamId else -1)
        }
        teamId = 0
        teamMap.forEach {
            if (teamMap.get(it.key) == -1) {
                teamMap.put(it.key, teamId++)
            }
            //Log.d(TAG, "tempMap: ${it.key}, ${it.value}")
        }
        return teamMap
    }

    fun selectRank(): MutableMap<Int, Int> {
        val tempList = mutableListOf<Int>() // to shuffle
        val rankMap = mutableMapOf<Int, Int>()
        for (key in _map.keys) {
            tempList.add(key)
        }
        tempList.shuffle()
        _map.forEach {
            rankMap.put(it.key, tempList[it.key] + 1) // +1 is for except 0
        }
        return rankMap
    }

    fun print() {
        _map.forEach { point ->
            Log.d(TAG, "touchPoint:(${point.value.x},${point.value.y})")
        }
    }
}