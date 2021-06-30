package com.eee.www.nugasa.ui

import android.content.Context
import android.graphics.Canvas
import android.os.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.eee.www.nugasa.R
import com.eee.www.nugasa.model.FingerMap
import com.eee.www.nugasa.ui.CanvasView.Constants.ANIM_REPEAT_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.ANIM_START_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_ANIM
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_PICK
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_RESET
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_SNACKBAR
import com.eee.www.nugasa.ui.CanvasView.Constants.PICK_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.SNACKBAR_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.RESET_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.SOUND_DELAYED_MILLIS
import com.eee.www.nugasa.utils.FingerPicker
import com.eee.www.nugasa.utils.SoundEffector
import com.eee.www.nugasa.utils.TAG
import kotlin.properties.Delegates

class CanvasView : View, Handler.Callback, MediatedView {
    private object Constants {
        const val MESSAGE_PICK = 0
        const val MESSAGE_ANIM = 1
        const val MESSAGE_RESET = 2
        const val MESSAGE_SNACKBAR = 3

        const val PICK_DELAYED_MILLIS = 3000L
        const val ANIM_START_DELAYED_MILLIS = 300L
        const val ANIM_REPEAT_DELAYED_MILLIS = 15L
        const val RESET_DELAYED_MILLIS = 3000L
        const val SOUND_DELAYED_MILLIS = 1000L
        const val SNACKBAR_DELAYED_MILLIS = 2000L
    }

    override var mediator: Mediator? = null

    lateinit var fingerPicker: FingerPicker
    var fingerCount = 1

    val fingerMap = FingerMap()

    private val eventHandler = Handler(Looper.getMainLooper(), this)

    private var shouldKeepDrawn by Delegates.notNull<Boolean>()

    private val soundEffector = SoundEffector(context)
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    init {
        resetAll()
    }

    private fun resetAll() {
        mediator?.setPressed(false)
        fingerMap.clear()
        shouldKeepDrawn = false
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
                stopPressedJobs()
                triggerPressedJobs()
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
                stopPressedJobs()
                if (!reset) {
                    triggerPressedJobs()
                }
                invalidate()
                return true
            }
        }
        return false
    }

    private fun addNewPoint(event: MotionEvent) {
        if (fingerMap.isFull()) {
            return
        }
        // first touch!
        if (fingerMap.isEmpty()) {
            mediator?.setPressed(true)
        }
        val pointerId = fingerMap.add(event)
        Log.d(TAG, "addNewPoint : $pointerId")
    }

    private fun stopPressedJobs() {
        stopSound()
        stopSnackbar()
        stopSelect()
        stopAnim()
    }

    private fun stopSound() {
        soundEffector.stop()
    }

    private fun stopSnackbar() {
        if (eventHandler.hasMessages(MESSAGE_SNACKBAR)) {
            eventHandler.removeMessages(MESSAGE_SNACKBAR)
        }
    }

    private fun stopSelect() {
        if (eventHandler.hasMessages(MESSAGE_PICK)) {
            eventHandler.removeMessages(MESSAGE_PICK)
        }
    }

    private fun stopAnim() {
        if (eventHandler.hasMessages(MESSAGE_ANIM)) {
            eventHandler.removeMessages(MESSAGE_ANIM)
        }
    }

    private fun triggerPressedJobs() {
        if (canSelect()) {
            triggerSound()
            triggerSelect()
            triggerAnim()
        } else {
            triggerSnackbar()
        }
    }

    private fun canSelect(): Boolean {
        return fingerMap.size > fingerCount
    }

    private fun triggerSound() {
        soundEffector.playTriggerInMillis(SOUND_DELAYED_MILLIS)
    }

    private fun triggerSelect() {
        eventHandler.sendEmptyMessageDelayed(MESSAGE_PICK, PICK_DELAYED_MILLIS)
    }

    private fun triggerAnim() {
        eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, ANIM_START_DELAYED_MILLIS)
    }

    private fun triggerSnackbar() {
        eventHandler.sendEmptyMessageDelayed(MESSAGE_SNACKBAR, SNACKBAR_DELAYED_MILLIS)
    }

    private fun movePoint(event: MotionEvent) {
        fingerMap.move(event)
        Log.d(TAG, "movePoint")
    }

    private fun removePoint(event: MotionEvent) {
        val pointerId = fingerMap.remove(event)
        Log.d(TAG, "removePoint : $pointerId")
    }

    private fun resetAllIfEmptyPoint(): Boolean {
        if (fingerMap.isEmpty()) {
            resetAll()
            return true
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (shouldKeepDrawn) {
            fingerPicker.drawSelected(canvas)
        } else {
            fingerPicker.draw(canvas)
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        return when (msg.what) {
            MESSAGE_PICK -> {
                doPick(fingerCount)

                playSelectSound()

                stopAnim()
                keepDrawnAwhile()
                invalidate()

                doVibrate()
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
            MESSAGE_SNACKBAR -> {
                showToast()
                true
            }
            else -> false
        }
    }

    private fun doPick(fingerCount: Int) {
        fingerPicker.pick(context, fingerCount)
    }

    private fun playSelectSound() {
        soundEffector.playSelect()
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
        if (!eventHandler.hasMessages(MESSAGE_ANIM)) {
            eventHandler.sendEmptyMessageDelayed(MESSAGE_ANIM, ANIM_REPEAT_DELAYED_MILLIS)
        }
    }

    private fun showToast() {
        CenterSnackbar.showShort(this, R.string.pickImpossibleMessage)
    }

    fun destroy() {
        soundEffector.release()
    }
}