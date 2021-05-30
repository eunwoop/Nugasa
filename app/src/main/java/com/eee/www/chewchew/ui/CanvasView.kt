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
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.TAG
import com.eee.www.chewchew.ui.CanvasView.CanvasViewConstants.WAITING_TIME
import com.eee.www.chewchew.utils.FingerColors
import com.eee.www.chewchew.utils.ViewUtils

class CanvasView : View, Handler.Callback {
    object CanvasViewConstants {
        const val TAG = "CanvasView"
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

    private var mTouchPointMap = mutableMapOf<Int, PointF>()
    private var mColorList = FingerColors.shuffle(context)
    private var mSelected = arrayListOf<Int>()

    private val paint = Paint()
    private val mHandler = Handler(Looper.getMainLooper(), this)

    private val MIN_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_PX.toFloat())
    private val MAX_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_MAX_PX.toFloat())
    private val SELECTED_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_SELECTED_PX.toFloat())

    private var curCircleSize = MIN_CIRCLE_SIZE
    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;

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
            mHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 15)
            return true
        }
        return false
    }

    private fun doAnimation() {
        curCircleSize++
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isFingerSelected()) {
            drawSelectedCircle(canvas)
        }
        drawCirclesOnTouch(canvas)
    }

    private fun drawCirclesOnTouch(canvas: Canvas){
        curCircleSize = if (curCircleSize >= MAX_CIRCLE_SIZE) MIN_CIRCLE_SIZE else curCircleSize
        for (index in 0..mTouchPointMap.size) {
            if (index >= MAX_TOUCH) {
                return;
            }

            val point = mTouchPointMap[index];
            paint.color = mColorList[index];
            if (point != null) {
                canvas.drawCircle(
                        point.x, point.y,
                        curCircleSize, paint
                )
            }
        }
    }

    private fun drawSelectedCircle(canvas: Canvas) {
        mSelected.forEach {
            val point = mTouchPointMap[it]
            paint.color = mColorList[it]
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
        if (!mHandler.hasMessages(MESSAGE_ANIM)){
            mHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 1000)
        }
    }

    private fun addNewPoint(event: MotionEvent) {
        if (isFingerSelected()) {
            return
        }
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        Log.d(TAG, "addNewPoint : $pointerId")
        if (mTouchPointMap[pointerId] == null) {
            mTouchPointMap[pointerId] = PointF(event.getX(pointerIndex), event.getY(pointerIndex))
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
            mTouchPointMap[pointerId]?.run {
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)
            }
        }
        invalidate()
    }

    private fun removePoint(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        mTouchPointMap.remove(pointerId)
        Log.d(TAG, "removePoint : $pointerId")

        //when last point removed
        if (mTouchPointMap.isEmpty()) {
            mSelected.clear()
            shuffleColor()
            fingerPressed.value = false
            stopAnim()
            curCircleSize = MIN_CIRCLE_SIZE
            invalidate()
            return;
        }
        triggerSelect()
        invalidate()
    }

    private fun shuffleColor() {
        mColorList = FingerColors.shuffle(context)
    }

    private fun pickN(n: Int) {
        if (mTouchPointMap.isEmpty()) {
            return
        }
        for (i in 0 until mTouchPointMap.size) {
            mSelected.add(i)
        }
        mSelected.shuffle()
        for (i in 0 until (mTouchPointMap.size - n)) {
            mSelected.removeAt(0)
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
        if (mHandler.hasMessages(MESSAGE_ANIM)){
            mHandler.removeMessages(MESSAGE_ANIM)
        }
    }

    private fun isFingerSelected(): Boolean {
        return mSelected.isNotEmpty()
    }

    private fun triggerSelect() {
        if (mHandler.hasMessages(MESSAGE_PICK)) {
            mHandler.removeMessages(MESSAGE_PICK)
        }
        if (mTouchPointMap.size > fingerCount) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_PICK, WAITING_TIME)
        }
    }

    private fun printPointerMap() {
        mTouchPointMap.forEach { point ->
            Log.d(TAG, "mTouchPointList:" + "(" + point.value.x + "," + point.value.y + ")")
        }
    }
}