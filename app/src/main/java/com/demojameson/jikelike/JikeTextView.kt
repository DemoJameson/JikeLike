package com.demojameson.jikelike

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.demojameson.jikelike.extensions.sp

/**
 * author : DemoJameson
 * time   : 2017/10/17
 * desc   : 模仿即刻的点赞效果，实现只有改变的文字上下滚动
 */

class JikeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  private var prevText: String = ""
  private var text: String = ""

  private var animationType = AnimationType.NONE

  fun getText() = text

  fun setText(value: String, animationType: AnimationType = AnimationType.UP) {
    this.animationType = animationType

    requestLayout()
    processText(value, text)
    prevText = text
    text = value

    if (animationType == AnimationType.NONE) {
      animator.duration = 0
    } else {
      animator.duration = 400
    }
    animator.start()
  }

  private lateinit var chars: List<TextColumn>

  private val paint = Paint().apply {
    isAntiAlias = true
    color = Color.BLACK
    textSize = context.sp(12).toFloat()
    style = Paint.Style.FILL
  }

  private var centerY = 0f

  private val animator = ValueAnimator.ofFloat(0f, 1f)

  init {
    context.obtainStyledAttributes(attrs, R.styleable.JikeTextView).apply {
      val color = getColor(R.styleable.JikeTextView_textColor, Color.BLACK)
      val textSize = getDimension(R.styleable.JikeTextView_textSize, context.sp(12).toFloat())
      val text = getString(R.styleable.JikeTextView_text) ?: ""
      paint.color = color
      paint.textSize = textSize
      setText(text, AnimationType.NONE)
    }.recycle()

    initAnimation()
  }

  private fun processText(text: String, prevText: String) {
    val prevChars = prevText.toCharArray()
    var text = text

    // 如果旧字符比较长，则新字符后面加空格
    for (i in 0..(prevText.length - text.length)) {
      text += " "
    }

    chars = text.toCharArray().mapIndexed { index, c ->
      val prevChar = try {
        prevChars[index].toString()
      } catch (e: IndexOutOfBoundsException) {
        ""
      }

      val x = if (index == 0) {
        0f
      } else {
        paint.measureText(text.subSequence(0, index).toString())
      }
      TextColumn(c.toString(), prevChar, x)
    }
  }

  private fun initAnimation() {
    animator.addUpdateListener {
      invalidate()
    }
    animator.addListener(object: SimpleAnimatorListener(){
      override fun onAnimationEnd(animation: Animator?) {
        // 确保以现有字符串长度测量控件大小
        prevText = ""
        requestLayout()
      }
    })
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    centerY = paint.fontMetrics.run {
      paddingTop - top
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
    val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)


    var height: Int = (paddingTop + paddingBottom + paint.fontMetrics.bottom - paint.fontMetrics.top).toInt()
    var width: Int = (paddingLeft + paddingRight + Math.max(paint.measureText(text), paint.measureText(prevText))).toInt()

    if (widthMode == View.MeasureSpec.EXACTLY) {
      width = widthSize
    }

    if (heightMode == View.MeasureSpec.EXACTLY) {
      height = heightSize
    }

    setMeasuredDimension(width, height)
  }

  override fun onDraw(canvas: Canvas) {
    canvas.translate(paddingLeft.toFloat(), centerY)

    chars.forEach { textColumn ->
      if (textColumn.changed) {
        paint.alpha = 255
        canvas.drawText(textColumn.text, textColumn.x, 0f, paint)
      } else {
        val animatedFraction = animator.animatedFraction

        val textY = if (animationType == AnimationType.UP) {
          paddingBottom - paint.fontMetrics.top
        } else {
          paint.fontMetrics.top - paddingTop
        }

        val prevTextY = paddingBottom - paddingTop - textY

        paint.alpha = (255 * animatedFraction).toInt()
        canvas.drawText(textColumn.text, textColumn.x, textY * (1 - animatedFraction), paint)

        paint.alpha = 255 - (255 * animatedFraction).toInt()
        canvas.drawText(textColumn.prevText, textColumn.x, prevTextY * animatedFraction, paint)
      }
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    animator.removeAllUpdateListeners()
  }

  enum class AnimationType {
    UP, DOWN, NONE
  }
}

data class TextColumn(val text: String, val prevText: String, val x: Float) {
  val changed = text == prevText
}
