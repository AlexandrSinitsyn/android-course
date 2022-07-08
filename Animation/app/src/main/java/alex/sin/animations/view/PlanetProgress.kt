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
import kotlin.math.PI
import kotlin.math.min

class PlanetProgress
@JvmOverloads
constructor(
    context: Context, attrs: AttributeSet, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var color: Int
    private var scale: Float
    private var angleSpeed: Int

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PlanetProgress, defStyleAttr, defStyleRes)

        try {
            color = a.getColor(R.styleable.PlanetProgress_color, Color.BLACK)
            scale = a.getFloat(R.styleable.PlanetProgress_scale, 1f)
            angleSpeed = a.getInt(R.styleable.PlanetProgress_angleSpeed, 10)
        } finally {
            a.recycle()
        }
    }

    private val sun = RectF()

    private val firstPlanet = RectF()
    private val secondPlanet = RectF()

    private var firstPlanetRotationAngle = 0f
    private var secondPlanetRotationAngle = 0f

    private fun updateAngle(angle: Float, first: Boolean): Float {
        var res = angle

        res += (10 * PI / 180).toFloat() * angleSpeed * (if (first) 2 else 1)
        if (res > 360) {
            res -= 360
        }
        invalidate()

        return res
    }

    private val size = scale * 16

    private val center = size / 2

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = this@PlanetProgress.color }

    private val firstPlanetAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = (2 * PI * scale / angleSpeed).toLong()
        addUpdateListener { firstPlanetRotationAngle = updateAngle(firstPlanetRotationAngle, true) }
    }

    private val secondPlanetAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = (2 * PI * scale / angleSpeed).toLong()
        addUpdateListener { secondPlanetRotationAngle = updateAngle(secondPlanetRotationAngle, false) }
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

    private fun drawPlanet(canvas: Canvas, rectangle: RectF,
                                centerX: Float, centerY: Float, rotationAngle: Float) {
        canvas.rotate(rotationAngle, center, center)
        rectangle.set(centerX - scale, centerY - scale, centerX + scale, centerY + scale)
        canvas.drawRoundRect(rectangle, scale, scale, paint)
    }

    private fun drawOrbit(canvas: Canvas, radius: Float) {
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(center, center, radius, paint)
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawPlanet(canvas, sun, center, center, 0f)

        drawPlanet(canvas, firstPlanet, center, center - scale * 3, firstPlanetRotationAngle)
        drawPlanet(canvas, secondPlanet, center, center - scale * 6, secondPlanetRotationAngle)

        drawOrbit(canvas, 3 * scale)
        drawOrbit(canvas, 6 * scale)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        AnimatorSet().apply {
            playTogether(firstPlanetAnimator, secondPlanetAnimator)

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
