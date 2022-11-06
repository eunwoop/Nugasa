package com.eee.www.nugasa.utils

import android.content.Context
import android.graphics.Canvas
import com.eee.www.nugasa.model.FingerMap

abstract class FingerPicker(protected val fingerMap: FingerMap) {

    lateinit var selectedMap: Map<Int, Int>

    abstract fun pick(count: Int)

    abstract fun draw(canvas: Canvas)

    abstract fun drawSelected(canvas: Canvas)

    abstract fun reset(context: Context)
}

class PickFingerPicker(context: Context, fingerMap: FingerMap) : FingerPicker(fingerMap) {

    private val fingerDrawer = PickFingerDrawer(context)

    override fun pick(count: Int) {
        selectedMap = fingerMap.select(count)
    }

    override fun draw(canvas: Canvas) {
        fingerMap.map.forEach {
            fingerDrawer.draw(canvas, it.key, it.value)
        }
        fingerDrawer.scaleCircle()
    }

    override fun drawSelected(canvas: Canvas) {
        fingerMap.map.forEach {
            val isSelected = selectedMap[it.key] == 1
            if (isSelected) {
                fingerDrawer.drawSelected(canvas, it.key, it.value)
            }
        }
        fingerDrawer.scaleSelectedCircle()
    }

    override fun reset(context: Context) {
        fingerDrawer.init(context)
    }
}

class TeamFingerPicker(context: Context, fingerMap: FingerMap) : FingerPicker(fingerMap) {

    private val fingerDrawer = TeamFingerDrawer(context)

    override fun pick(count: Int) {
        selectedMap = fingerMap.selectTeam(count)
    }

    override fun draw(canvas: Canvas) {
        fingerMap.map.forEach {
            fingerDrawer.draw(canvas, it.value)
        }
        fingerDrawer.scaleCircle()
    }

    override fun drawSelected(canvas: Canvas) {
        fingerMap.map.forEach {
            val team = selectedMap[it.key] ?: 0
            fingerDrawer.drawSelected(canvas, it.value, team)
        }
        fingerDrawer.scaleSelectedCircle()
    }

    override fun reset(context: Context) {
        fingerDrawer.init(context)
    }
}

class RankFingerPicker(context: Context, fingerMap: FingerMap) : FingerPicker(fingerMap) {

    private val fingerDrawer = RankFingerDrawer(context)

    override fun pick(count: Int) {
        selectedMap = fingerMap.selectRank()
    }

    override fun draw(canvas: Canvas) {
        fingerMap.map.forEach {
            fingerDrawer.draw(canvas, it.key, it.value)
        }
        fingerDrawer.scaleCircle()
    }

    override fun drawSelected(canvas: Canvas) {
        fingerMap.map.forEach {
            val rank = selectedMap[it.key] ?: -1
            fingerDrawer.drawSelected(canvas, it.key, it.value, rank)
        }
        fingerDrawer.scaleSelectedCircle()
    }

    override fun reset(context: Context) {
        fingerDrawer.init(context)
    }
}