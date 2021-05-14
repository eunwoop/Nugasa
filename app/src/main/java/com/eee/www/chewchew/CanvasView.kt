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
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MAX_TOUCH
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.MESSAGE_PICK
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.TAG
import com.eee.www.chewchew.CanvasView.CanvasViewConstants.WAITING_TIME


class CanvasView : View {
    object CanvasViewConstants {
        const val TAG = "CanvasView"
        const val MAX_TOUCH = 10
        const val WAITING_TIME = 3000L
        const val MESSAGE_PICK = 0
    }

    val fingerPressed = MutableLiveData<Boolean>()
    var fingerCount = 1

    private var mTouchPointMap = mutableMapOf<Int, PointF>()
    private var mColorList = arrayListOf<Int>()
    private var mSelected = arrayListOf<Int>()
    private var mContext : Context? = context;
    private var mHandler = Handler(Looper.getMainLooper(), Handler.Callback {
        if (it.what == MESSAGE_PICK) {
            pickN(fingerCount)
            return@Callback true
        }
        return@Callback false
    });
    private var mCardBitmap = mutableListOf<Bitmap?>()
    private var mBigCardBitmap = mutableListOf<Bitmap?>()

    init {
        mColorList = (context?.let { ColorLoader.getInstance(it).getColorList() }
                as ArrayList<Int>?)!!

        for (i in 0..9) {
            val bitmap = mContext?.let {
                getBitmapFromVectorDrawable(it, R.drawable.ic_credit_card, mColorList[i])
            }
            mCardBitmap.add(bitmap)
            mBigCardBitmap.add(Bitmap.createScaledBitmap(bitmap!!,
                bitmap!!.width*2, bitmap!!.height*2, false))
        }
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
        for (index in 0 .. mTouchPointMap.size) {
            if (index >= MAX_TOUCH) {
                return;
            }

            val point = mTouchPointMap[index];
            paint.color = mColorList[index];
            if (point != null) {
                val x = point.x- ((mCardBitmap[index]?.width ?: 0))/2
                val y = point.y- ((mCardBitmap[index]?.height ?: 0))/2
                mCardBitmap[index]?.let { canvas.drawBitmap(it, x, y, paint) }
            }
        }

        if (isFingerSelected()) {
            mSelected.forEach {
                val point = mTouchPointMap[it]
                paint.color = mColorList[it]
                if (point != null) {
                    val x = point.x- ((mBigCardBitmap[it]?.width ?: 0))/2
                    val y = point.y- ((mBigCardBitmap[it]?.height ?: 0))/2
                    mBigCardBitmap[it]?.let { its -> canvas.drawBitmap(its, x, y, paint) }
                }
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
                    triggerSelect()
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    moveCoord(event)
                    invalidate()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP,
                MotionEvent.ACTION_POINTER_2_UP, MotionEvent.ACTION_POINTER_3_UP -> {
                    removeCoord(event, pointerIndex, pointerId)
                    triggerSelect()
                    invalidate()
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "IllegalArgumentException happened in onTouch$e")
        }
        return true
    }

    private fun addNewCoord(event: MotionEvent, pointerIndex: Int, pointerId: Int) {
        if (isFingerSelected()) {
            return;
        }
        if (mTouchPointMap[pointerId] == null) {
            mTouchPointMap[pointerId] = PointF(event.getX(pointerId), event.getY(pointerId))
            fingerPressed.value = true
        }
    }

    private fun moveCoord(event: MotionEvent) {
        if (isFingerSelected()) {
            return;
        }
        for (i in 0 until event.pointerCount) {
            if (mTouchPointMap.size - 1 < i) {
                mTouchPointMap[event.getPointerId(i)] = PointF(event.getX(i), event.getY(i))
            } else {
                (mTouchPointMap[event.getPointerId(i)])?.x = event.getX(i)
                (mTouchPointMap[event.getPointerId(i)])?.y = event.getY(i)
            }
        }
    }

    private fun removeCoord(event: MotionEvent, pointerIndex: Int, pointerId: Int) {
        mTouchPointMap.remove(pointerId)
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

    private fun isFingerSelected() : Boolean {
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

    private fun dpToPx(context:Context?, dp:Float) : Float {
        if (context != null) {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, context.resources.displayMetrics
            )
        };
        return dp;
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId:Int, color:Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null;
        drawable.setColorFilter(color, PorterDuff.Mode.OVERLAY)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888);

        val canvas = Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.width, canvas.height);
        drawable.draw(canvas);

        return bitmap;
    }
}