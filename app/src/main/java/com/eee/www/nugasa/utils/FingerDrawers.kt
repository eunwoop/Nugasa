package com.eee.www.nugasa.utils

import android.content.Context
import android.graphics.*
import androidx.core.content.res.ResourcesCompat
import com.eee.www.nugasa.R

open class FingerDrawer(context: Context) {

    protected val MIN_CIRCLE_SIZE = ViewUtils.dpToPx(context, 50F)
    protected val MAX_CIRCLE_SIZE = ViewUtils.dpToPx(context, 60F)
    protected var circleSize = MIN_CIRCLE_SIZE

    protected val paint = Paint().apply { isAntiAlias = true }

    init {
        init(context)
    }

    fun init(context: Context) {
        circleSize = MIN_CIRCLE_SIZE
        FingerColors.shuffle(context)
    }

    fun scaleCircle() {
        circleSize = if (circleSize < MAX_CIRCLE_SIZE) circleSize + 1 else MIN_CIRCLE_SIZE
    }

    protected fun drawCircle(canvas: Canvas, point: PointF?, circleColor: Int) {
        point?.also {
            canvas.drawCircle(
                it.x,
                it.y,
                circleSize,
                paint.apply { color = circleColor })
        }
    }

    protected fun drawCircle(canvas: Canvas, point: PointF?, circleSize: Float, circleColor: Int) {
        point?.also {
            canvas.drawCircle(
                it.x,
                it.y,
                circleSize,
                paint.apply { color = circleColor })
        }
    }
}

class PickFingerDrawer(context: Context) : FingerDrawer(context) {

    private val SELECTED_CIRCLE_SIZE = ViewUtils.dpToPx(context, 80F)

    fun draw(canvas: Canvas, pointerId: Int, point: PointF) {
        drawCircle(canvas, point, FingerColors.randomColor(pointerId))
    }

    fun drawSelected(canvas: Canvas, pointerId: Int, point: PointF) {
        drawCircle(canvas, point, SELECTED_CIRCLE_SIZE, FingerColors.randomColor(pointerId))
    }
}

class TeamFingerDrawer(context: Context) : FingerDrawer(context) {

    private val grayColor = ResourcesCompat.getColor(context.resources, R.color.gray, null)

    fun draw(canvas: Canvas, point: PointF) {
        drawCircle(canvas, point, grayColor)
    }

    fun drawSelected(canvas: Canvas, point: PointF, team: Int) {
        drawCircle(canvas, point, MIN_CIRCLE_SIZE, FingerColors.randomColor(team))
    }
}

class RankFingerDrawer(context: Context) : FingerDrawer(context) {

    private val RANK_TEXT_SIZE = 220F
    private val numberColor =
        ResourcesCompat.getColor(context.resources, R.color.rank_text_color, null)
    private val shadowColor =
        ResourcesCompat.getColor(context.resources, R.color.rank_text_shadow_color, null)
    private val textPaint = Paint().apply {
        color = numberColor
        textSize = RANK_TEXT_SIZE
        textAlign = Paint.Align.LEFT
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(10f, 2f, 2f, shadowColor)
    }

    fun draw(canvas: Canvas, pointerId: Int, point: PointF) {
        drawCircle(canvas, point, FingerColors.randomColor(pointerId))
    }

    fun drawSelected(canvas: Canvas, pointerId: Int, point: PointF, rank: Int) {
        drawCircle(canvas, point, MIN_CIRCLE_SIZE, FingerColors.randomColor(pointerId))
        drawRank(canvas, point, rank)
    }

    private fun drawRank(canvas: Canvas, point: PointF?, rank: Int) {
        point?.also {
            val position = getPositionOfRank(point, rank.toString())
            canvas.drawText(rank.toString(), position.x, position.y, textPaint)
        }
    }

    private fun getPositionOfRank(point: PointF, rankText: String): PointF {
        val textRect = Rect()
        textPaint.getTextBounds(rankText, 0, rankText.length, textRect)

        val x: Float = point.x - textRect.width() / 2f - textRect.left
        val y: Float = point.y + textRect.height() / 2f - textRect.bottom
        return PointF(x, y)
    }
}