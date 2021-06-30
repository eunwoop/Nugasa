package com.eee.www.nugasa.utils

import android.content.Context
import android.graphics.Canvas
import com.eee.www.nugasa.model.FingerMap

abstract class FingerPicker(context: Context, protected val fingerMap: FingerMap) {

    open lateinit var selectedMap: Map<Int, Int>

    abstract fun pick(context: Context, count: Int)

    abstract fun draw(canvas: Canvas)

    abstract fun drawSelected(canvas: Canvas)
}

class PickFingerPicker(context: Context, fingerMap: FingerMap) : FingerPicker(context, fingerMap) {

    private val fingerDrawer = PickFingerDrawer(context)

    override fun pick(context: Context, count: Int) {
        fingerDrawer.init(context)
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
    }
}

class TeamFingerPicker(context: Context, fingerMap: FingerMap) : FingerPicker(context, fingerMap) {

    private val fingerDrawer = TeamFingerDrawer(context)

    override fun pick(context: Context, count: Int) {
        fingerDrawer.init(context)
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
    }
}

class RankFingerPicker(context: Context, fingerMap: FingerMap) : FingerPicker(context, fingerMap) {

    private val fingerDrawer = RankFingerDrawer(context)

    override fun pick(context: Context, count: Int) {
        fingerDrawer.init(context)
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
    }
}