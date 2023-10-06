package gb.com.cartrajectoryanimation.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import gb.com.cartrajectoryanimation.R
import kotlin.math.atan2

class DrawingView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    private var drawLength = 0f
    private var path = Path()
    private var carPosition = PointF()
    private lateinit var scaledCarIcon: Bitmap
    private var carRotation: Float = 0f

    private val pathPaint: Paint
    private val newWidth = 65

    init {
        pathPaint = initilizePathPaint()
        loadAndScaleCarIcon()
    }

    private fun initilizePathPaint(): Paint {
        return Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 20f
            pathEffect = DashPathEffect(floatArrayOf(30f, 10f), 0f)
        }
    }

    private fun loadAndScaleCarIcon() {
        val carIcon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.car_icon)
        val aspectRatio =carIcon.width.toFloat() / carIcon.height.toFloat()
        val newHeight = (newWidth / aspectRatio).toInt()
        scaledCarIcon = Bitmap.createScaledBitmap(carIcon, newWidth ,newHeight, false)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val lightBlue = Color.parseColor("#ADD8E6")
        val gradient = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
            lightBlue, Color.CYAN, Shader.TileMode.CLAMP)
        pathPaint.shader = gradient
    }


    private fun initPathAnimation() {
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 5000
        animator.addUpdateListener {
            drawLength = pathLength * it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    fun drawRandomPath() {
        path.reset()
        path.moveTo(0f, 0f)

        var x = 0f
        var y = 0f
        val xStep = width / 10f
        val yStep = width / 10f

        for (i in 1..20) {
            if (i % 2 == 1) {
                x += (if (Math.random() < 0.5) -1 else 1) * (xStep + (Math.random() * xStep - xStep / 2)
                    .toFloat())
                x = x.coerceAtLeast(0f).coerceAtMost(width.toFloat())
            } else {
                y += (if (Math.random() < 0.5) -1 else 1) * (yStep + (Math.random() * height - yStep / 2)
                    .toFloat())
                y = y.coerceAtLeast(0f).coerceAtMost(height.toFloat())
            }
            path.lineTo(x, y)
        }
        path.lineTo(width.toFloat(), height.toFloat())
        invalidate()

        initPathAnimation()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawPath(canvas)
        drawCarIcon(canvas)
    }

    private fun drawPath(canvas: Canvas?){
        val tempPath = Path()
        val pathMeasure = PathMeasure(path, false)
        pathMeasure.getSegment(0f, drawLength, tempPath, true)
        canvas?.drawPath(tempPath, pathPaint)
    }

    private fun drawCarIcon(canvas: Canvas?) {
        val matrix = Matrix()
        matrix.postRotate(carRotation, scaledCarIcon.width / 2f,scaledCarIcon.height / 2f)
        matrix.postTranslate(
            carPosition.x - scaledCarIcon.width / 2,
            carPosition.y - scaledCarIcon.height / 2
        )
        canvas?.drawBitmap(scaledCarIcon, matrix, null)
    }

    private fun setCarPosition(fraction: Float) {
        val pathMeasure = PathMeasure(path, false)
        val pathLength = pathMeasure.length
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        pathMeasure.getPosTan(pathLength * fraction, pos, tan)
        carPosition.x = pos[0]
        carPosition.y = pos[1]
        val degrees = (atan2(tan[1].toDouble(), tan[0].toDouble()) * 180.0 / Math.PI).toFloat()
        carRotation = degrees - 90
        invalidate()
    }

    fun animateCarAlongPath() {
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 15000
            addUpdateListener {
                val fraction = it.animatedValue as Float
                setCarPosition(fraction)
            }
        }
        animator.start()
    }
}