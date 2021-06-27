package com.eee.www.nugasa.ui

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
import com.eee.www.nugasa.MainActivity
import com.eee.www.nugasa.R
import com.eee.www.nugasa.model.FingerMap
import com.eee.www.nugasa.ui.CanvasView.Constants.ANIM_REPEAT_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.ANIM_START_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.CIRCLE_SIZE_MAX_PX
import com.eee.www.nugasa.ui.CanvasView.Constants.CIRCLE_SIZE_PX
import com.eee.www.nugasa.ui.CanvasView.Constants.CIRCLE_SIZE_SELECTED_PX
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_ANIM
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_PICK
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_RESET
import com.eee.www.nugasa.ui.CanvasView.Constants.MESSAGE_SNACKBAR
import com.eee.www.nugasa.ui.CanvasView.Constants.PICK_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.SNACKBAR_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.PICK_RESET_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.RANK_TEXT_SIZE
import com.eee.www.nugasa.ui.CanvasView.Constants.SOUND_DELAYED_MILLIS
import com.eee.www.nugasa.ui.CanvasView.Constants.TEAM_RESET_DELAYED_MILLIS
import com.eee.www.nugasa.utils.FingerColors
import com.eee.www.nugasa.utils.SoundEffector
import com.eee.www.nugasa.utils.TAG
import com.eee.www.nugasa.utils.ViewUtils
import kotlin.properties.Delegates

class CanvasView : View, Handler.Callback {
    private object Constants {
        const val CIRCLE_SIZE_PX = 50
        const val CIRCLE_SIZE_MAX_PX = 60
        const val CIRCLE_SIZE_SELECTED_PX = 100
        const val RANK_TEXT_SIZE = 80F

        const val MESSAGE_PICK = 0
        const val MESSAGE_ANIM = 1
        const val MESSAGE_RESET = 2
        const val MESSAGE_SNACKBAR = 3

        const val PICK_DELAYED_MILLIS = 3000L
        const val ANIM_START_DELAYED_MILLIS = 300L
        const val ANIM_REPEAT_DELAYED_MILLIS = 15L
        const val PICK_RESET_DELAYED_MILLIS = 2000L
        const val TEAM_RESET_DELAYED_MILLIS = 4000L
        const val SOUND_DELAYED_MILLIS = 1000L
        const val SNACKBAR_DELAYED_MILLIS = 2000L
    }

    val fingerPressed = MutableLiveData<Boolean>()
    var fingerCount = 1
    var mode = 0

    private lateinit var touchPointMap: FingerMap
    private lateinit var selectedPointList: List<Int>
    private lateinit var selectedTeamMap: Map<Int, Int>
    private lateinit var selectedRankMap: Map<Int, Int>

    private val eventHandler = Handler(Looper.getMainLooper(), this)

    private val paint = Paint()
    private var shouldKeepDrawn by Delegates.notNull<Boolean>()

    private val MIN_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_PX.toFloat())
    private val MAX_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_MAX_PX.toFloat())
    private val SELECTED_CIRCLE_SIZE = ViewUtils.dpToPx(context, CIRCLE_SIZE_SELECTED_PX.toFloat())
    private var circleSize by Delegates.notNull<Float>()

    private val soundEffector = SoundEffector(context)
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    init {
        resetAll()
        paint.isAntiAlias = true
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
        if (isFingerSelected() || touchPointMap.isFull()) {
            return
        }
        // first touch!
        if (touchPointMap.isEmpty()) {
            fingerPressed.value = true
        }
        val pointerId = touchPointMap.add(event)
        Log.d(TAG, "addNewPoint : $pointerId")
    }

    private fun isFingerSelected(): Boolean {
        return selectedPointList.isNotEmpty()
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
        if (mode == MainActivity.Constants.MENU_RANK) {
            return true;
        }
        return touchPointMap.size > fingerCount
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (shouldKeepDrawn) {
            drawSelected(canvas)
        } else {
            drawAll(canvas)
        }
    }

    private fun drawAll(canvas: Canvas) {
        circleSize = if (circleSize >= MAX_CIRCLE_SIZE) MIN_CIRCLE_SIZE else circleSize
        val grayColor = resources.getColor(R.color.gray)
        touchPointMap.map.forEach {
            when(mode) {
                MainActivity.Constants.MENU_PICK -> {
                    drawCircle(canvas, it.key, it.value)
                }
                MainActivity.Constants.MENU_TEAM -> {
                    drawCircle(canvas, it.key, it.value, grayColor)
                }
                MainActivity.Constants.MENU_RANK -> {
                    drawCircle(canvas, it.key, it.value)
                }
            }
        }
    }

    private fun drawSelected(canvas: Canvas) {
        when(mode){
            MainActivity.Constants.MENU_PICK -> {
                circleSize = SELECTED_CIRCLE_SIZE
                touchPointMap.map.forEach {
                    val isSelected = selectedPointList.contains(it.key)
                    if (isSelected) {
                        drawCircle(canvas, it.key, it.value)
                    }
                }
            }
            MainActivity.Constants.MENU_TEAM -> {
                touchPointMap.map.forEach {
                    val team = selectedTeamMap.get(it.key) ?: 0
                    val teamColor = FingerColors.randomColor(team)
                    drawCircle(canvas, it.key, it.value, teamColor)
                }
            }
            MainActivity.Constants.MENU_RANK -> {
                touchPointMap.map.forEach {
                    drawCircle(canvas, it.key, it.value)
                }
                touchPointMap.map.forEach {
                    drawNumber(canvas, it.key, it.value)
                }
            }
        }
    }

    private fun drawCircle(canvas: Canvas, pointerId: Int, point: PointF?) {
        paint.color = FingerColors.randomColor(pointerId)
        point?.also { canvas.drawCircle(it.x, it.y, circleSize, paint) }
    }

    private fun drawCircle(canvas: Canvas, pointerId: Int, point: PointF?, color: Int) {
        paint.color = color
        point?.also { canvas.drawCircle(it.x, it.y, circleSize, paint) }
    }

    private fun drawNumber(canvas: Canvas, pointerId: Int, point: PointF?) {
        paint.color = resources.getColor(R.color.rank_text_color)
        point?.also {
            paint.textSize = RANK_TEXT_SIZE;
            canvas.drawText(selectedRankMap[pointerId].toString(),
                it.x-15F, it.y - circleSize -5, paint);
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
        when (mode) {
            MainActivity.Constants.MENU_PICK ->
                pickN(fingerCount)
            MainActivity.Constants.MENU_TEAM ->
                pickTeam(fingerCount)
            MainActivity.Constants.MENU_RANK ->
                pickRank()
        }
    }

    private fun pickN(n: Int) {
        selectedPointList = touchPointMap.select(n)
    }

    private fun pickTeam(n: Int) {
        selectedTeamMap = touchPointMap.selectTeam(n)
    }

    private fun pickRank() {
        selectedRankMap = touchPointMap.selectRank()
    }

    private fun playSelectSound() {
        soundEffector.playSelect()
    }

    private fun keepDrawnAwhile() {
        shouldKeepDrawn = true

        if (eventHandler.hasMessages(MESSAGE_RESET)) {
            eventHandler.removeMessages(MESSAGE_RESET)
        }
        val delayMillis = if (mode == MainActivity.Constants.MENU_TEAM)
            TEAM_RESET_DELAYED_MILLIS else PICK_RESET_DELAYED_MILLIS
        eventHandler.sendEmptyMessageDelayed(MESSAGE_RESET, delayMillis)
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

    private fun showToast() {
        CenterSnackbar.showShort(this, R.string.pickImpossibleMessage)
    }

    fun destroy() {
        soundEffector.release()
    }
}