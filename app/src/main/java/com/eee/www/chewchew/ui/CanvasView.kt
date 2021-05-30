package com.eee.www.chewchew.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.CIRCLE_SIZE_MAX_PX
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.CIRCLE_SIZE_PX
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.CIRCLE_SIZE_SELECTED_PX
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.MAX_TOUCH
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.MESSAGE_ANIM
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.MESSAGE_PICK
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.WAITING_TIME
import com.eee.www.chewchew.utils.FingerColors
import com.eee.www.chewchew.utils.TAG
import com.eee.www.chewchew.utils.ViewUtils

class CanvasView : View, Handler.Callback {
    object CanvasViewConstants {
        const val MAX_TOUCH = 10
        const val CIRCLE_SIZE_PX = 50
        const val CIRCLE_SIZE_MAX_PX = 60
        const val CIRCLE_SIZE_SELECTED_PX = 100
        const val WAITING_TIME = 3000L

        const val MESSAGE_PICK: Int = 0
        const val MESSAGE_ANIM: Int = 1
    }

    val fingerPressed = MutableLiveData<Boolean>()
    var fingerCount = 1

    private var touchPointMap = mutableMapOf<Int, PointF>()
    private var selectedPointList = arrayListOf<Int>()

    private var colorList = FingerColors.shuffle(context)
    private val paint = Paint()

    private val eventHandler = Handler(Looper.getMainLooper(), this)

    private val MIN_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_PX.toFloat())
    private val MAX_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_MAX_PX.toFloat())
    private val SELECTED_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_SELECTED_PX.toFloat())
    private var circleSize = MIN_CIRCLE_SIZE

    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == MESSAGE_PICK) {
            pickN(fingerCount)
            invalidate()
            return true
        } else if (msg.what == MESSAGE_ANIM) {
            doAnimation()
            invalidate()
            eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 15)
            return true
        }
        return false
    }

    private fun doAnimation() {
        circleSize++
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isFingerSelected()) {
            drawSelectedCircle(canvas)
        }
        drawCirclesOnTouch(canvas)
    }

    private fun drawCirclesOnTouch(canvas: Canvas){
        circleSize = if (circleSize >= MAX_CIRCLE_SIZE) MIN_CIRCLE_SIZE else circleSize
        for (index in 0..touchPointMap.size) {
            if (index >= MAX_TOUCH) {
                return
            }

            val point = touchPointMap[index];
            paint.color = colorList[index];
            if (point != null) {
                canvas.drawCircle(
                        point.x, point.y,
                        circleSize, paint
                )
            }
        }
    }

    private fun drawSelectedCircle(canvas: Canvas) {
        selectedPointList.forEach {
            val point = touchPointMap[it]
            paint.color = colorList[it]
            if (point != null) {
                canvas.drawCircle(
                        point.x, point.y,
                        SELECTED_CIRCLE_SIZE, paint
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d(TAG, "onTouchEvent : ACTION_DOWN")
                addNewPoint(event)
                startAnim()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "onTouchEvent : ACTION_MOVE")
                movePoint(event)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                Log.d(TAG, "onTouchEvent : ACTION_UP")
                removePoint(event)
                return true
            }
        }
        return false
    }

    private fun startAnim() {
        if (!eventHandler.hasMessages(MESSAGE_ANIM)){
            eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 1000)
        }
    }

    private fun addNewPoint(event: MotionEvent) {
        if (isFingerSelected()) {
            return
        }
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        Log.d(TAG, "addNewPoint : $pointerId")
        if (touchPointMap[pointerId] == null) {
            touchPointMap[pointerId] = PointF(event.getX(pointerIndex), event.getY(pointerIndex))
            fingerPressed.value = true
        }
        triggerSelect()
        invalidate()
    }

    private fun movePoint(event: MotionEvent) {
        if (isFingerSelected()) {
            return
        }
        Log.d(TAG, "movePoint")
        for (pointerIndex in 0 until event.pointerCount) {
            val pointerId = event.getPointerId(pointerIndex)
            touchPointMap[pointerId]?.run {
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)
            }
        }
        invalidate()
    }

    private fun removePoint(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        touchPointMap.remove(pointerId)
        Log.d(TAG, "removePoint : $pointerId")

        //when last point removed
        if (touchPointMap.isEmpty()) {
            selectedPointList.clear()
            shuffleColor()
            fingerPressed.value = false
            stopAnim()
            circleSize = MIN_CIRCLE_SIZE
            invalidate()
            return;
        }
        triggerSelect()
        invalidate()
    }

    private fun shuffleColor() {
        colorList = FingerColors.shuffle(context)
    }

    private fun pickN(n: Int) {
        if (touchPointMap.isEmpty()) {
            return
        }
        for (i in 0 until touchPointMap.size) {
            selectedPointList.add(i)
        }
        selectedPointList.shuffle()
        for (i in 0 until (touchPointMap.size - n)) {
            selectedPointList.removeAt(0)
        }
        stopAnim()
        doVibrate()
    }

    private fun doVibrate() {
        Log.d(TAG, "do Vibrate!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(100, 100)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(100)
        }
    }

    private fun stopAnim() {
        if (eventHandler.hasMessages(MESSAGE_ANIM)){
            eventHandler.removeMessages(MESSAGE_ANIM)
        }
    }

    private fun isFingerSelected(): Boolean {
        return selectedPointList.isNotEmpty()
    }

    private fun triggerSelect() {
        if (eventHandler.hasMessages(MESSAGE_PICK)) {
            eventHandler.removeMessages(MESSAGE_PICK)
        }
        if (touchPointMap.size > fingerCount) {
            eventHandler.sendEmptyMessageDelayed(MESSAGE_PICK, WAITING_TIME)
        }
    }

    private fun printPointerMap() {
        touchPointMap.forEach { point ->
            Log.d(TAG, "touchPoint:(${point.value.x},${point.value.y})")
        }
    }
}