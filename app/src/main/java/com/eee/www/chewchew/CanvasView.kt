package com.eee.www.chewchew

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.MotionEvent
import android.view.View

class CanvasView(context: Context?) : View(context) {
    private lateinit var mTouchPoint: Point
    private var mSize = 100

    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        if (!this::mTouchPoint.isInitialized) {
            return
        }
        canvas.drawRect(
            mTouchPoint.x - mSize.toFloat(), mTouchPoint.y - mSize.toFloat(),
            mTouchPoint.x + mSize.toFloat(), mTouchPoint.y + mSize.toFloat(), paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                if (!this::mTouchPoint.isInitialized)
                    mTouchPoint = Point()
                mTouchPoint.x = event.x.toInt()
                mTouchPoint.y = event.y.toInt()
                invalidate()
            }
        }
        return true
    }
}