package com.eee.www.chewchew

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.CIRCLE_SIZE
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MAX_TOUCH
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MESSAGE_PICK
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.SELECTED_CIRCLE_SIZE
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.TAG
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.WAITING_TIME

class CanvasView : View {
    object CanvasViewConstants {
        const val TAG = "CanvasView"
        const val MAX_TOUCH = 10
        const val CIRCLE_SIZE = 50
        const val SELECTED_CIRCLE_SIZE = 100
        const val WAITING_TIME = 3000L

        const val MESSAGE_PICK = 0
    }

    val fingerPressed = MutableLiveData<Boolean>()
    var fingerCount = 1

    private var mTouchPointMap = mutableMapOf<Int, PointF>()
    private var mColorList = arrayListOf<Int>()
    private var mSelected = arrayListOf<Int>()
    private var mContext: Context? = context
    private var mHandler = Handler(Looper.getMainLooper(), Handler.Callback {
        if (it.what == MESSAGE_PICK) {
            pickN(fingerCount)
            return@Callback true
        }
        return@Callback false
    })

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

    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        for (index in 0..mTouchPointMap.size) {
            if (index >= MAX_TOUCH) {
                return;
            }

            val point = mTouchPointMap[index];
            paint.color = mColorList[index];
            if (point != null) {
                canvas.drawCircle(
                    point.x, point.y,
                    dpToPx(mContext, CIRCLE_SIZE.toFloat()), paint
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_2_UP, MotionEvent.ACTION_POINTER_3_DOWN -> {
                Log.d(TAG, "onTouchEvent : ACTION_DOWN")
                if (event.pointerCount == 1) {
                    mColorList = (context?.let {
                        ColorLoader.getInstance(it).getColorList()
                    } as ArrayList<Int>?)!!
                }
                addNewCoord(event)
                triggerSelect()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "onTouchEvent : ACTION_MOVE")
                moveCoord(event)
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_POINTER_2_UP, MotionEvent.ACTION_POINTER_3_UP -> {
                Log.d(TAG, "onTouchEvent : ACTION_UP")
                removeCoord(event)
                triggerSelect()
                invalidate()
            }
        }
        return true
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
        invalidate()
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