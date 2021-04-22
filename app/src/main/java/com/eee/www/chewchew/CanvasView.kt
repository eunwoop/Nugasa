package com.eee.www.chewchew

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.CIRCLE_SIZE
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MAX_TOUCH
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.TAG
import java.lang.IllegalArgumentException

class CanvasView(context: Context?) : View(context) {
    object CanvasViewConstants {
        const val TAG = "CanvasView"
        const val MAX_TOUCH = 10
        const val CIRCLE_SIZE = 100
    }

    private var mTouchPointMap = mutableMapOf<Int, PointF>()
    private var mColorList = arrayListOf<Int>()

    init {
        mColorList = (context?.let { ColorLoader.getInstance(it).getColorList() }
                as ArrayList<Int>?)!!
    }

    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        for (index in 0 .. mTouchPointMap.size) {
            if (index >= MAX_TOUCH) {
                return;
            }

            val point = mTouchPointMap[index];
            paint.color = mColorList[index];
            if (point != null) {
                canvas.drawCircle(point.x, point.y, CIRCLE_SIZE.toFloat(), paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            val pointerIndex = event.actionIndex
            val pointerId = event.getPointerId(pointerIndex)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN,
                MotionEvent.ACTION_POINTER_2_UP, MotionEvent.ACTION_POINTER_3_DOWN -> {
                    if (event.pointerCount == 1) {
                        mColorList = (context?.let {
                            ColorLoader.getInstance(it).getColorList()
                        } as ArrayList<Int>?)!!
                    }
                    addNewCoord(event, pointerIndex, pointerId)
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    moveCoord(event)
                    invalidate()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP,
                MotionEvent.ACTION_POINTER_2_UP, MotionEvent.ACTION_POINTER_3_UP -> {
                    removeCoord(event, pointerIndex, pointerId)
                    invalidate()
                }
            }
        } catch (e : IllegalArgumentException) {
            Log.e(TAG, "IllegalArgumentException happened in onTouch$e")
        }

        return true
    }

    private fun addNewCoord(event: MotionEvent, pointerIndex: Int, pointerId: Int) {
        if (mTouchPointMap[pointerId] == null) {
            mTouchPointMap[pointerId] = PointF(event.getX(pointerId), event.getY(pointerId))
        }
    }

    private fun moveCoord(event: MotionEvent) {
        for (i in 0 until event.pointerCount) {
            if (mTouchPointMap.size - 1 < i) {
                mTouchPointMap[event.getPointerId(i)] = PointF(event.getX(i), event.getY(i))
            } else {
                (mTouchPointMap[event.getPointerId(i)])?.x = event.getX(i)
                (mTouchPointMap[event.getPointerId(i)])?.y = event.getY(i)
            }
        }
    }

    private fun removeCoord(event: MotionEvent,  pointerIndex: Int, pointerId: Int) {
        mTouchPointMap.remove(pointerId)
    }

    private fun printPointerMap() {
        mTouchPointMap.forEach { point ->
            Log.d(TAG, "mTouchPointList:" + "(" + point.value.x + "," + point.value.y + ")")
        }
    }
}