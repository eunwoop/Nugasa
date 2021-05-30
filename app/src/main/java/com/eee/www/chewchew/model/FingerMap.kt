package com.eee.www.chewchew.model

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import com.eee.www.chewchew.utils.TAG

class FingerMap {

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
    
    fun getPoint(pointerId: Int): PointF? = _map[pointerId]

    fun select(n: Int): List<Int> {
        val selected = mutableListOf<Int>()
        for (i in 0 until _map.size) {
            selected.add(i)
        }
        selected.shuffle()
        for (i in 0 until (_map.size - n)) {
            selected.removeAt(0)
        }
        return selected
    }

    fun print() {
        map.forEach { point ->
            Log.d(TAG, "touchPoint:(${point.value.x},${point.value.y})")
        }
    }
}