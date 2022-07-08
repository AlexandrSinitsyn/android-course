package alex.sin.animations.view

import alex.sin.animations.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class Cross
@JvmOverloads
constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    View(context, attrs, defStyleAttr, defStyleRes) {

    private var color: Int
    private var scale: Float
    private var animationDelay: Long
    private var animationDuration: Long

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Cross, defStyleAttr, defStyleRes)

        try {
            color = a.getColor(R.styleable.Cross_color, Color.BLACK)
            scale = a.getFloat(R.styleable.Cross_scale, 1f)
            animationDelay = a.getInt(R.styleable.Cross_animationDelay, 100).toLong()
            animationDuration = a.getInt(R.styleable.Cross_animationDuration, 300).toLong()
        } finally {
            a.recycle()
        }
    }

    private val verticalRectangle = RectF()
    private val horizontalRectangle = RectF()

    private val leftDot = RectF()
    private val topDot = RectF()
    private val rightDot = RectF()
    private val bottomDot = RectF()

    private var radius = 40 * scale
    private val lineWeight = radius / 4
    private val cornerRadius = lineWeight * scale
    private val maxScale = 1.5f


    private var leftDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var topDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var rightDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var bottomDotScale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private var rotationAngle = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val offset = radius / 2
    /*offset + crossLeft + crossRight + space + dotLeft + space + dotRight + offset*/
    private val expectedWidth = offset + radius * 2 + radius + radius * 3 + offset
    /*offset + dotTop + space + dotBottom + offset*/
    private val expectedHeight = offset + radius * 3 + offset

    private val crossCenterX = radius + offset
    private val crossCenterY = expectedHeight / 2

    private val dotsCenterX = expectedWidth - radius * 2 - offset
    private val dotsCenterY = expectedHeight / 2

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = this@Cross.color }

    private var animator: Animator? = null
    private val crossAnimator = ValueAnimator.ofFloat(0.0F, 180F).apply {
        duration = animationDuration
        addUpdateListener {
            rotationAngle = it.animatedValue as Float
        }
    }

    private val leftDotAnimator = ValueAnimator.ofFloat(1f, 1f, 1f, 1f, 1f, maxScale, 1f).apply {
        duration = animationDuration
        addUpdateListener {
            leftDotScale = it.animatedValue as Float
        }
    }

    private val topDotAnimator = ValueAnimator.ofFloat(1f, 1f, maxScale, 1f, 1f, 1f, 1f).apply {
        duration = animationDuration
        addUpdateListener {
            topDotScale = it.animatedValue as Float
        }
    }

    private val rightDotAnimator = ValueAnimator.ofFloat(1f, 1f, 1f, maxScale, 1f, 1f, 1f).apply {
        duration = animationDuration
        addUpdateListener {
            rightDotScale = it.animatedValue as Float
        }
    }

    private val bottomDotAnimator = ValueAnimator.ofFloat(1f, 1f, 1f, 1f, maxScale, 1f, 1f).apply {
        duration = animationDuration
        addUpdateListener {
            bottomDotScale = it.animatedValue as Float
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        val w = when (widthMode) {
            MeasureSpec.AT_MOST -> min(width, expectedWidth)
            MeasureSpec.EXACTLY -> width
            MeasureSpec.UNSPECIFIED -> expectedWidth
            else -> expectedWidth
        }
        val h = when (heightMode) {
            MeasureSpec.AT_MOST -> min(height, expectedHeight)
            MeasureSpec.EXACTLY -> height
            MeasureSpec.UNSPECIFIED -> expectedHeight
            else -> expectedHeight
        }

        setMeasuredDimension(w.toInt(), h.toInt())
    }

    private fun onDrawCrossLine(canvas: Canvas, rectangle: RectF,
                                lengthX: Float, lengthY: Float, cornerRadius: Float) {
        val save = canvas.save()

        canvas.rotate(rotationAngle, crossCenterX, crossCenterY)
        rectangle.set(crossCenterX - lengthX, crossCenterY - lengthY, crossCenterX + lengthX, crossCenterY + lengthY)
        canvas.drawRoundRect(rectangle, cornerRadius, cornerRadius, paint)

        canvas.restoreToCount(save)
    }


    private fun onDrawDot(canvas: Canvas, dot: RectF,
                          cx: Float, cy: Float, scale: Float,
                          cornerRadius: Float) {
        val save = canvas.save()

        canvas.scale(scale, scale, cx, cy)
        val halfRadius = radius / 2
        dot.set(cx - halfRadius, cy - halfRadius, cx + halfRadius, cy + halfRadius)
        canvas.drawRoundRect(dot, cornerRadius, cornerRadius, paint)

        canvas.restoreToCount(save)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        onDrawCrossLine(canvas, verticalRectangle, radius, lineWeight, cornerRadius)
        onDrawCrossLine(canvas, horizontalRectangle, lineWeight, radius, cornerRadius)

        onDrawDot(canvas, leftDot, dotsCenterX - radius, dotsCenterY, leftDotScale, radius)
        onDrawDot(canvas, topDot, dotsCenterX, dotsCenterY - radius, topDotScale, radius)
        onDrawDot(canvas, rightDot, dotsCenterX + radius, dotsCenterY, rightDotScale, radius)
        onDrawDot(canvas, bottomDot, dotsCenterX, dotsCenterY + radius, bottomDotScale, radius)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        animator?.cancel()

        animator = AnimatorSet().apply {
            playTogether(crossAnimator, leftDotAnimator, topDotAnimator, rightDotAnimator, bottomDotAnimator)

            startDelay = animationDelay

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    startDelay = animationDelay
                    start()
                }
            })
            start()
        }
    }
}