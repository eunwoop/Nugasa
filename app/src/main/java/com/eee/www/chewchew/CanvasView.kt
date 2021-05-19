package com.eee.www.chewchew

import android.content.Context
import android.graphics.*
import android.os.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.CIRCLE_SIZE
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.CIRCLE_SIZE_2
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MAX_TOUCH
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MESSAGE_ANIM
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MESSAGE_PICK
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.SELECTED_CIRCLE_SIZE
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.TAG
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.WAITING_TIME

class CanvasView : View, Handler.Callback {
    object CanvasViewConstants {
        const val TAG = "CanvasView"
        const val MAX_TOUCH = 10
        const val CIRCLE_SIZE = 50
        const val CIRCLE_SIZE_2 = 60
        const val SELECTED_CIRCLE_SIZE = 100
        const val WAITING_TIME = 3000L

        const val MESSAGE_PICK: Int = 0
        const val MESSAGE_ANIM: Int = 1
    }

    val fingerPressed = MutableLiveData<Boolean>()
    var fingerCount = 1

    private var mTouchPointMap = mutableMapOf<Int, PointF>()
    private var mColorList = arrayListOf<Int>()
    private var mSelected = arrayListOf<Int>()
    private var mContext: Context? = context
    private val paint = Paint()
    private val mHandler = Handler(Looper.getMainLooper(), this)
    private val circleSize = dpToPx(mContext, CIRCLE_SIZE.toFloat())
    private val circleSize2 = dpToPx(mContext, CIRCLE_SIZE_2.toFloat())
    var curCircleSize = circleSize
    val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;

    init {
        mColorList = (context?.let { ColorLoader.getInstance(it).getColorList() }
                as ArrayList<Int>?)!!
    }

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
            return true
        } else if (msg.what == MESSAGE_ANIM) {
            doAnimation()
            invalidate()
            Log.d(TAG, "handleMessage : send MESSAGE_ANIM")
            mHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 15)
            return true
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        curCircleSize = if (curCircleSize >= circleSize2) circleSize else curCircleSize
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

        if (isFingerSelected()) {
            mSelected.forEach {
                val point = mTouchPointMap[it]
                paint.color = mColorList[it]
                if (point != null) {
                    canvas.drawCircle(
                        point.x, point.y,
                        dpToPx(mContext, SELECTED_CIRCLE_SIZE.toFloat()), paint
                    )
                }
            }
        }
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

    private fun doAnimation() {
        curCircleSize++
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d(TAG, "onTouchEvent : ACTION_DOWN")
                if (event.pointerCount == 1) {
                    mColorList = (context?.let {
                        ColorLoader.getInstance(it).getColorList()
                    } as ArrayList<Int>?)!!
                }
                addNewCoord(event)
                triggerSelect()
                invalidate()
                if (!mHandler.hasMessages(MESSAGE_ANIM)){
                    Log.d(TAG, "onTouchEvent : send MESSAGE_ANIM")
                    mHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, 1000)
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "onTouchEvent : ACTION_MOVE")
                moveCoord(event)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                Log.d(TAG, "onTouchEvent : ACTION_UP")
                removeCoord(event)
                triggerSelect()
                invalidate()
                return true
            }
        }
        return false
    }

    private fun addNewCoord(event: MotionEvent) {
        if (isFingerSelected()) {
            return
        }
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        Log.d(TAG, "addNewCoord : $pointerId")
        if (mTouchPointMap[pointerId] == null) {
            mTouchPointMap[pointerId] = PointF(event.getX(pointerIndex), event.getY(pointerIndex))
            fingerPressed.value = true
        }
    }

    private fun moveCoord(event: MotionEvent) {
        if (isFingerSelected()) {
            return
        }

        Log.d(TAG, "moveCoord")
        for (pointerIndex in 0 until event.pointerCount) {
            val pointerId = event.getPointerId(pointerIndex)
            mTouchPointMap[pointerId]?.run {
                x = event.getX(pointerIndex)
                y = event.getY(pointerIndex)
            }
        }
    }

    private fun removeCoord(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        mTouchPointMap.remove(pointerId)
        Log.d(TAG, "removeCoord : $pointerId")
        if (mTouchPointMap.isEmpty()) {
            mSelected.clear()
            fingerPressed.value = false
            stopAnim()
            curCircleSize = circleSize
        }
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
        invalidate()
    }

    private fun stopAnim() {
        if (mHandler.hasMessages(MESSAGE_ANIM)){
            Log.d(TAG, "onTouchEvent : remove MESSAGE_ANIM")
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

    private fun dpToPx(context: Context?, dp: Float): Float {
        if (context != null) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, context.resources.displayMetrics
            )
        }
        return dp
    }
}