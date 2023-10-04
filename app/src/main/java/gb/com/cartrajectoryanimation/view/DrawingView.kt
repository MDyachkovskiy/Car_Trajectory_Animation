package gb.com.cartrajectoryanimation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class DrawingView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    private val pathPaint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val carPaint: Paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private var path = Path()
    private var carPosition = PointF()

    fun drawRandomPath() {
        path.reset()
        path.moveTo(0f, 0f)

        for(i in 1..4) {
            val x = (width / 5f) * i
            val y = (Math.random() * height).toFloat()
            path.lineTo(x,y)
        }
        path.lineTo(width.toFloat(), height.toFloat())
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, pathPaint)
        canvas?.drawCircle(carPosition.x, carPosition.y, 20f, carPaint)
    }

    fun setCarPosition(fraction: Float) {
        carPosition.x = fraction * width
        carPosition.y = fraction * height
        invalidate()
    }
}