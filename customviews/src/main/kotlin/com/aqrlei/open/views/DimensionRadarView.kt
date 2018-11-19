package com.aqrlei.open.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.aqrlei.open.views.util.DensityUtil
import kotlin.math.PI

/**
 * @author aqrlei on 2018/11/16
 */
/**
 * @description: 雷达维度图
 * scoreLevel 评分级别
 * maxScore 最高评分
 * dimensionTextSize
 * dimensionTextList 评分维度 >=3
 * dimensionTextColorList
 * dimensionScoreLevelList 维度评分 this.size == dimensionTextList.size
 * dimensionRadarBackgroundColor
 * dimensionRadarScoreColorList
 * diagonalLineColor
 * diagonalLineWidth
 * sideLineColor
 * sideLineWidth
 * */
//TODO
class DimensionRadarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) : View(context, attrs) {
    companion object {
        const val DEFAULT_BACKGROUND_COLOR = Color.WHITE
        const val DEFAULT_LINE_COLOR = Color.GRAY
        const val DEFAULT_SCORE_COLOR = Color.GREEN

    }

    private var radius: Float = 0F
    private var centerX: Float = 0F
    private var centerY: Float = 0F
    private var scoreLevel: Int = 2
    private var maxScore: Int = 100
    private var rotateDegree: Float = 0F
    private var dimensionTextSize: Int = DensityUtil.dip2px(14F)
    private val dimensionTextList = ArrayList<String>()
    private val dimensionTextColorList = ArrayList<Int>()
    private val dimensionScoreLevelList = ArrayList<Float>()
    private var dimensionRadarBackgroundColor: Int = DEFAULT_BACKGROUND_COLOR
    private val dimensionRadarScoreColorList = ArrayList<Int>()
    private var diagonalLineColor: Int = DEFAULT_LINE_COLOR
    private var diagonalLineWidth: Int = DensityUtil.dip2px(1F)
    private var sideLineColor: Int = DEFAULT_LINE_COLOR
    private var sideLineWidth: Int = DensityUtil.dip2px(1F)


    private val linePaint = Paint()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.DimensionRadarView)?.run {
            scoreLevel = getInteger(R.styleable.DimensionRadarView_scoreLevel, scoreLevel)
            maxScore = getInteger(R.styleable.DimensionRadarView_maxScore, maxScore)
            dimensionTextSize = getDimensionPixelSize(
                    R.styleable.DimensionRadarView_dimensionTextSize,
                    dimensionTextSize)
            rotateDegree = getFloat(R.styleable.DimensionRadarView_dimensionRotateDegree, rotateDegree)
            getTextArray(R.styleable.DimensionRadarView_dimensionTextList)?.forEach {
                dimensionTextList.add(it.toString())
            }
            getTextArray(R.styleable.DimensionRadarView_dimensionTextColorList)?.forEach {
                dimensionTextColorList.add(
                        try {
                            Color.parseColor(it.toString())
                        } catch (e: IllegalArgumentException) {
                            Color.BLACK
                        })
            }
            getTextArray(R.styleable.DimensionRadarView_dimensionScoreLevelList)?.forEach {
                dimensionScoreLevelList.add(it.toString().toFloatOrNull() ?: 0F)
            }
            dimensionRadarBackgroundColor = getColor(
                    R.styleable.DimensionRadarView_dimensionRadarBackgroundColor,
                    dimensionRadarBackgroundColor)
            getTextArray(R.styleable.DimensionRadarView_dimensionRadarScoreColorList)?.forEach {
                dimensionRadarScoreColorList.add(
                        try {
                            Color.parseColor(it.toString())
                        } catch (e: java.lang.IllegalArgumentException) {
                            Color.GRAY
                        })
            }
            diagonalLineColor = getColor(R.styleable.DimensionRadarView_diagonalLineColor, diagonalLineColor)
            diagonalLineWidth = getDimensionPixelSize(R.styleable.DimensionRadarView_diagonalLineWidth, diagonalLineWidth)

            sideLineColor = getColor(R.styleable.DimensionRadarView_sideLineColor, sideLineColor)
            sideLineWidth = getDimensionPixelSize(R.styleable.DimensionRadarView_sideLineWidth, sideLineWidth)

            recycle()
        }

        with(linePaint) {
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (Math.min(w, h) - 2 * dimensionTextSize) / 2 * 0.9F
        centerX = w / 2.0F
        centerY = h / 2.0F
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        drawScoreLevel(canvas)
        canvas.restore()
        val matrix = canvas.matrix
        matrix.setRotate(rotateDegree, centerX, centerY)
        canvas.matrix = matrix
    }

    private fun drawScoreLevel(canvas: Canvas) {
        val level = 5
        for (i in 0 until level) {
            val scale = (1.0F - 1.0F / (level - i))
            drawLines(canvas)
            canvas.scale(scale, scale, centerX, centerY)
        }
    }

    private fun drawLines(canvas: Canvas) {
        val divideCount = 6
        val a = 360.0 / divideCount
        val b = ((a / 2) / 360.0) * 2 * PI
        val c = (((180 - a) / 2.0) / 360.0) * 2 * PI
        val side = radius * Math.abs(Math.sin(b)) * 2
        val diagonalPath = Path()
        val sidePath = Path()

        for (i in 0 until divideCount) {
            diagonalPath.reset()
            sidePath.reset()
            val diagonalX = centerX
            val diagonalY = centerY - radius
            val sideX = diagonalX + (side * Math.abs(Math.sin(c))).toFloat()
            val sideY = diagonalY + (side * Math.abs(Math.cos(c))).toFloat()

            diagonalPath.moveTo(centerX, centerY)
            diagonalPath.lineTo(diagonalX, diagonalY)
            canvas.drawPath(diagonalPath, linePaint.apply {
                color = diagonalLineColor
            })
            sidePath.moveTo(diagonalX, diagonalY)
            sidePath.lineTo(sideX, sideY)
            canvas.drawPath(sidePath, linePaint.apply {
                color = sideLineColor
            })
            canvas.rotate(a.toFloat(), centerX, centerY)
        }

    }
}