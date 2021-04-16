package com.eee.www.chewchew

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class CanvasView(context: Context?) : View(context) {
    private lateinit var mTouchPoint: PointF
    private var mSize = 100

    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        if (!this::mTouchPoint.isInitialized) {
            return
        }
        canvas.drawCircle(mTouchPoint.x, mTouchPoint.y, mSize.toFloat(), paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                if (!this::mTouchPoint.isInitialized)
                    mTouchPoint = PointF()
                mTouchPoint.x = event.x
                mTouchPoint.y = event.y
                invalidate()
            }
        }
        return true
    }
}