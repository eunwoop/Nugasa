package com.eee.www.chewchew.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.eee.www.chewchew.model.FingerMap
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.ANIM_REPEAT_DELAYED_MILLIS
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.ANIM_START_DELAYED_MILLIS
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.CIRCLE_SIZE_MAX_PX
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.CIRCLE_SIZE_PX
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.CIRCLE_SIZE_SELECTED_PX
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.MESSAGE_ANIM
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.MESSAGE_RESET
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.MESSAGE_PICK
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.PICK_DELAYED_MILLIS
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.RESET_DELAYED_MILLIS
import com.eee.www.chewchew.utils.FingerColors
import com.eee.www.chewchew.utils.TAG
import com.eee.www.chewchew.utils.ViewUtils
import kotlin.properties.Delegates

class CanvasView : View, Handler.Callback {
    object CanvasViewConstants {
        const val CIRCLE_SIZE_PX = 50
        const val CIRCLE_SIZE_MAX_PX = 60
        const val CIRCLE_SIZE_SELECTED_PX = 100

        const val MESSAGE_PICK: Int = 0
        const val MESSAGE_ANIM: Int = 1
        const val MESSAGE_RESET: Int = 2

        const val PICK_DELAYED_MILLIS = 3000L
        const val ANIM_START_DELAYED_MILLIS = 300L
        const val ANIM_REPEAT_DELAYED_MILLIS = 15L
        const val RESET_DELAYED_MILLIS = 2000L
    }

    val fingerPressed = MutableLiveData<Boolean>()
    var fingerCount = 1

    private lateinit var touchPointMap: FingerMap
    private lateinit var selectedPointList: List<Int>

    private val eventHandler = Handler(Looper.getMainLooper(), this)

    private val paint = Paint()
    private var shouldKeepDrawn by Delegates.notNull<Boolean>()

    private val MIN_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_PX.toFloat())
    private val MAX_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_MAX_PX.toFloat())
    private val SELECTED_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_SELECTED_PX.toFloat())
    private var circleSize by Delegates.notNull<Float>()

    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    init {
        resetAll()
    }

    private fun resetAll() {
        fingerPressed.value = false
        touchPointMap = FingerMap()
        selectedPointList = listOf()
        shouldKeepDrawn = false
        circleSize = MIN_CIRCLE_SIZE
        shuffleColor()
    }

    private fun shuffleColor() {
        FingerColors.shuffle(context)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (shouldKeepDrawn) {
            return false
        }

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
        if (isFingerSelected() || touchPointMap.isFull()) {
            return
        }
        val pointerId = touchPointMap.add(event)
        Log.d(TAG, "addNewPoint : $pointerId")
    }

    private fun triggerAnim() {
        if (eventHandler.hasMessages(MESSAGE_ANIM)) {
            eventHandler.removeMessages(MESSAGE_ANIM)
        }
        eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, ANIM_START_DELAYED_MILLIS)
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
            resetAll()
            return true
        }
        return false
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
            eventHandler.sendEmptyMessageDelayed(MESSAGE_PICK, PICK_DELAYED_MILLIS)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCirclesOnTouch(canvas)
    }

    private fun drawCirclesOnTouch(canvas: Canvas) {
        circleSize = if (circleSize >= MAX_CIRCLE_SIZE) MIN_CIRCLE_SIZE else circleSize

        touchPointMap.map.forEach {
            val isSelected = selectedPointList.contains(it.key)
            val point = touchPointMap.map[it.key]
            paint.color = FingerColors.randomColor(it.key)
            if (isSelected) {
                point?.also { canvas.drawCircle(it.x, it.y, SELECTED_CIRCLE_SIZE, paint) }
            } else {
                point?.also { canvas.drawCircle(it.x, it.y, circleSize, paint) }
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        return when (msg.what) {
            MESSAGE_PICK -> {
                pickN(fingerCount)
                keepDrawnAwhile()
                stopAnim()
                doVibrate()
                invalidate()
                true
            }
            MESSAGE_ANIM -> {
                doAnim()
                invalidate()
                true
            }
            MESSAGE_RESET -> {
                resetAll()
                invalidate()
                true
            }
            else -> false
        }
    }

    private fun pickN(n: Int) {
        selectedPointList = touchPointMap.select(n)
    }

    private fun keepDrawnAwhile() {
        shouldKeepDrawn = true

        if (eventHandler.hasMessages(MESSAGE_RESET)) {
            eventHandler.removeMessages(MESSAGE_RESET)
        }
        eventHandler.sendEmptyMessageDelayed(MESSAGE_RESET, RESET_DELAYED_MILLIS)
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
            eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, ANIM_REPEAT_DELAYED_MILLIS)
        }
    }
}