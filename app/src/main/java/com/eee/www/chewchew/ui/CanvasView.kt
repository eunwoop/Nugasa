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
import com.eee.www.chewchew.model.FingerMap
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

    private var touchPointMap = FingerMap()
    private var selectedPointList = listOf<Int>()

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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d(TAG, "onTouchEvent : ACTION_DOWN")
                addNewPoint(event)
                triggerSelect()
                triggerAnim()
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "onTouchEvent : ACTION_MOVE")
                movePoint(event)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                Log.d(TAG, "onTouchEvent : ACTION_UP")
                removePoint(event)
                val reset = resetAllIfEmptyPoint()
                if (reset) {
                    stopSelect()
                    stopAnim()
                } else {
                    triggerAnim()
                    triggerSelect()
                }
                invalidate()
                return true
            }
        }
        return false
    }

    private fun addNewPoint(event: MotionEvent) {
        if (isFingerSelected()) {
            return
        }
        val pointerId = touchPointMap.add(event)
        Log.d(TAG, "addNewPoint : $pointerId")
    }

    private fun triggerAnim() {
        if (eventHandler.hasMessages(MESSAGE_ANIM)) {
            eventHandler.removeMessages(MESSAGE_ANIM)
        }
        eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 300)
    }

    private fun movePoint(event: MotionEvent) {
        if (isFingerSelected()) {
            return
        }
        touchPointMap.move(event)
        Log.d(TAG, "movePoint")
    }

    private fun removePoint(event: MotionEvent) {
        val pointerId = touchPointMap.remove(event)
        Log.d(TAG, "removePoint : $pointerId")
    }

    private fun resetAllIfEmptyPoint(): Boolean {
        if (touchPointMap.isEmpty()) {
            selectedPointList = listOf()
            circleSize = MIN_CIRCLE_SIZE
            shuffleColor()
            return true
        }
        return false
    }

    private fun shuffleColor() {
        colorList = FingerColors.shuffle(context)
    }

    private fun stopAnim() {
        if (eventHandler.hasMessages(MESSAGE_ANIM)) {
            eventHandler.removeMessages(MESSAGE_ANIM)
        }
    }

    private fun isFingerSelected(): Boolean {
        return selectedPointList.isNotEmpty()
    }

    private fun stopSelect() {
        fingerPressed.value = false

        if (eventHandler.hasMessages(MESSAGE_PICK)) {
            eventHandler.removeMessages(MESSAGE_PICK)
        }
    }

    private fun triggerSelect() {
        fingerPressed.value = true

        if (eventHandler.hasMessages(MESSAGE_PICK)) {
            eventHandler.removeMessages(MESSAGE_PICK)
        }
        if (touchPointMap.size > fingerCount) {
            eventHandler.sendEmptyMessageDelayed(MESSAGE_PICK, WAITING_TIME)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isFingerSelected()) {
            drawSelectedCircle(canvas)
        }
        drawCirclesOnTouch(canvas)
    }

    private fun drawSelectedCircle(canvas: Canvas) {
        selectedPointList.forEach {
            val point = touchPointMap.map[it]
            paint.color = colorList[it]
            if (point != null) {
                canvas.drawCircle(
                    point.x, point.y,
                    SELECTED_CIRCLE_SIZE, paint
                )
            }
        }
    }

    private fun drawCirclesOnTouch(canvas: Canvas) {
        circleSize = if (circleSize >= MAX_CIRCLE_SIZE) MIN_CIRCLE_SIZE else circleSize
        for (index in 0..touchPointMap.size) {
            if (index >= MAX_TOUCH) {
                return
            }

            val point = touchPointMap.map[index]
            paint.color = colorList[index]
            if (point != null) {
                canvas.drawCircle(
                    point.x, point.y,
                    circleSize, paint
                )
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == MESSAGE_PICK) {
            pickN(fingerCount)
            stopAnim()
            doVibrate()
            invalidate()
            return true
        } else if (msg.what == MESSAGE_ANIM) {
            doAnim()
            invalidate()
            return true
        }
        return false
    }

    private fun pickN(n: Int) {
        selectedPointList = touchPointMap.select(n)
    }

    private fun doVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(100, 100)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(100)
        }
    }

    private fun doAnim() {
        circleSize++

        if (!eventHandler.hasMessages(MESSAGE_ANIM)) {
            eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 15)
        }
    }
}