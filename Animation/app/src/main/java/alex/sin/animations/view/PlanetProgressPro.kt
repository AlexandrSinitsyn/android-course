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
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class PlanetProgressPro
@JvmOverloads
constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    View(context, attrs, defStyleAttr, defStyleRes) {

    private var color: Int
    private var scale: Float
    private var planetRadius: Float
    private var orbitCount: Int
    private var angleSpeed: Int
    private var rotationDirectionClockwise: Boolean
    private var rotationSpeedRandom: Boolean

    private var planets: MutableList<Pair<RectF, Float>>
    private var animators: MutableList<Animator?>
    private var speeds: MutableList<Float>

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PlanetProgressPro, defStyleAttr, defStyleRes)

        try {
            color = a.getColor(R.styleable.PlanetProgressPro_color, Color.BLACK)
            scale = a.getFloat(R.styleable.PlanetProgressPro_scale, 1f) * 40
            planetRadius = a.getFloat(R.styleable.PlanetProgressPro_radius, scale)
            orbitCount = a.getInt(R.styleable.PlanetProgressPro_orbitCount, 3)
            angleSpeed = a.getInt(R.styleable.PlanetProgressPro_angleSpeed, 10)
            rotationDirectionClockwise = a.getBoolean(R.styleable.PlanetProgressPro_rotationDirectionClockwise, true)
            rotationSpeedRandom = a.getBoolean(R.styleable.PlanetProgressPro_rotationSpeedRandom, false)

            planets = MutableList(orbitCount) { Pair(RectF(), 0f) }
            animators = MutableList(orbitCount) { null }
            speeds = MutableList(orbitCount) { if (rotationSpeedRandom) Random.nextFloat() else (10 * PI / 180).toFloat() }
        } finally {
            a.recycle()
        }
    }

    private val earth = RectF()

    private fun updateAngle(index: Int): Pair<RectF, Float> {
        val planet = planets[index]
        var res = planet.second

        val step = speeds[index] * angleSpeed * (index + 1) / orbitCount
        res += step * (if (rotationDirectionClockwise) 1 else -1)
        if (res > 360) {
            res -= 360
        }

        invalidate()

        return Pair(planet.first, res)
    }

    private val radius = scale
    private val size = (radius * (2 + 3 * orbitCount - 1) + max(radius, planetRadius)) * 2

    private val center = size / 2

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = this@PlanetProgressPro.color }

    private fun getAnimator(index: Int) = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = (2 * PI * radius).toLong()
        addUpdateListener { planets[index] = updateAngle(index) }
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
        val save = canvas.save()

        canvas.rotate(rotationAngle, center, center)
        rectangle.set(centerX - planetRadius, centerY - planetRadius, centerX + planetRadius, centerY + planetRadius)
        canvas.drawRoundRect(rectangle, planetRadius, planetRadius, paint)

        canvas.restoreToCount(save)
    }

    private fun drawOrbit(canvas: Canvas, radius: Float) {
        val saved = paint.style
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(center, center, radius, paint)
        paint.style = saved
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawPlanet(canvas, earth, center, center, 0f)

        planets.forEachIndexed { ind, p ->
            drawPlanet(canvas, p.first, center, center - radius * 3 * (ind + 1), p.second)
            drawOrbit(canvas, radius * 3 * (ind + 1))
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        Log.i("Restore", "???")

        animators.forEach { it?.cancel() }

        for (i in 0 until animators.size) {
            animators[i] = AnimatorSet().apply {
                play(getAnimator(i))

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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        animators.forEach { it?.cancel() }
    }

    override fun onSaveInstanceState(): Parcelable {
        Log.i("Restore", "saved")

        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        planets.forEachIndexed { ind, p -> bundle.putFloat("angle-${ind}", p.second) }
        speeds.forEachIndexed { ind, s -> bundle.putFloat("speed-${ind}", s) }

        return bundle
    }

    override fun onRestoreInstanceState(parcelable: Parcelable?) {
        var state = parcelable

        Log.i("Restore", "start")

        if (state is Bundle) {
            Log.i("Restore", "is bundle")
            for (i in 0 until orbitCount) {
                planets[i] = Pair(RectF(), state.getFloat("angle-${i}"))
                Log.i("Restore", "i: $i >> ${planets[i].second}")
                speeds[i] = state.getFloat("speed-${i}")
            }

            state = state.getParcelable("superState")
        }

        super.onRestoreInstanceState(state)
    }
}