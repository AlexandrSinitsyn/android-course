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

class PulsatingDot
@JvmOverloads
constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var color: Int
    private var maxScale: Float
    private var animationDuration: Long

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PulsatingDot, defStyleAttr, defStyleRes)

        try {
            color = a.getColor(R.styleable.PulsatingDot_color, Color.BLACK)
            maxScale = a.getFloat(R.styleable.PulsatingDot_maxScale, 100f)
            animationDuration = a.getInt(R.styleable.PulsatingDot_animationDuration, 500).toLong()
        } finally {
            a.recycle()
        }
    }

    private val dot = RectF()

    private var scale = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val size = maxScale

    private val center = size / 2

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = this@PulsatingDot.color }

    private val rng = (0..maxScale.toInt()).map(Int::toFloat).toFloatArray()
    private val dotAnimator = ValueAnimator.ofFloat(*rng).apply {
        duration = animationDuration
        addUpdateListener {
            scale = it.animatedValue as Float
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        val w = when (widthMode) {
            MeasureSpec.AT_MOST -> min(width, size)
            MeasureSpec.EXACTLY -> width
            MeasureSpec.UNSPECIFIED -> size
            else -> size
        }
        val h = when (heightMode) {
            MeasureSpec.AT_MOST -> min(height, size)
            MeasureSpec.EXACTLY -> height
            MeasureSpec.UNSPECIFIED -> size
            else -> size
        }

        setMeasuredDimension(w.toInt(), h.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val save = canvas.save()

        val radius = scale / 2
        dot.set(center - radius, center - radius, center + radius, center + radius)
        canvas.drawRoundRect(dot, radius, radius, paint)

        canvas.restoreToCount(save)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        AnimatorSet().apply {
            playTogether(dotAnimator)

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    start()
                }
            })
            start()
        }
    }
}