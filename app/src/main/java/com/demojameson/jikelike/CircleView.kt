package com.demojameson.jikelike

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.demojameson.jikelike.extensions.dip

/**
 * author : DemoJameson
 * time   : 2017/10/19
 * desc   : 红色圆形的 View
 */
class CircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  val defaultStrokeWidth = context.dip(2).toFloat()

  var strokeWidth: Float = defaultStrokeWidth

    set(value) {
      field = value
      paint.strokeWidth = value
      invalidate()
    }

  private var paint: Paint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.STROKE
    color = ContextCompat.getColor(context, R.color.bright_red)
    strokeWidth = this@CircleView.strokeWidth
  }

  private var centerX:Float = 0f
  private var centerY:Float = 0f

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)

    centerX = w/2f
    centerY = h/2f
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawCircle(centerX, centerY, centerX.coerceAtMost(centerY) - strokeWidth, paint)
  }
}