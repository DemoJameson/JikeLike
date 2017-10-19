package com.demojameson.jikelike

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.view.animation.AccelerateDecelerateInterpolator
import com.demojameson.jikelike.extensions.layout
import com.demojameson.jikelike.extensions.scale
import kotlin.properties.Delegates

/**
 * author : DemoJameson
 * time   : 2017/10/17
 * desc   : 模仿即刻的点赞图标效果
 */
class JikeLikeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val unselectedImageView = ImageView(context).apply {
        setImageResource(R.drawable.ic_messages_like_unselected)
    }
    private val selectedImageView = ImageView(context).apply {
        setImageResource(R.drawable.ic_messages_like_selected)
    }
    private val shiningImageView = ImageView(context).apply {
        setImageResource(R.drawable.ic_messages_like_selected_shining)
    }
    private val circleImageView = CircleView(context).apply {
        alpha = 0f
    }

    private val uncheckedAnimator = ValueAnimator.ofFloat(0.8f, 1f)
    private val checkedAnimator = ValueAnimator.ofFloat(1f, 0.8f, 1.2f, 1f)

    private var checked = false

    init {
        addView(unselectedImageView)
        addView(selectedImageView)
        addView(shiningImageView)
        addView(circleImageView)

        context.obtainStyledAttributes(attrs, R.styleable.JikeLikeView).apply {
            checked = getBoolean(R.styleable.JikeLikeView_checked, false)
            if (checked) {
                unselectedImageView.alpha = 0f
            } else {
                selectedImageView.alpha = 0f
                shiningImageView.alpha = 0f
            }
        }.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val scale = 0.55f
        val selectedLeft = (1 - scale) * measuredWidth / 2
        val selectedTop = (1 - scale) * measuredHeight / 2
        val selectedRight = scale * measuredWidth + selectedLeft
        val selectedBottom = scale * measuredHeight + selectedTop

        unselectedImageView.layout(selectedLeft, selectedTop, selectedRight, selectedBottom)
        selectedImageView.layout(selectedLeft, selectedTop, selectedRight, selectedBottom)

        val shiningScale = 0.45f
        val shiningLeft = (1 - shiningScale) * measuredWidth / 2
        val shiningTop = 0f
        val shiningRight = shiningScale * measuredWidth + shiningLeft
        val shiningBottom = shiningScale * measuredHeight + shiningTop

        shiningImageView.layout(shiningLeft, shiningTop, shiningRight, shiningBottom)

        circleImageView.layout(0, 0, measuredWidth, measuredHeight)
    }

    fun getChecked() = checked

    fun setChecked(checked: Boolean) {
        if (this.checked == checked) {
            return
        }

        this.checked = checked

        if (checked) {
            showCheckedImage()
        } else {
            showUncheckedImage()
        }
    }

    private fun showCheckedImage() {
        checkedAnimator.run {
            removeAllUpdateListeners()

            addListener(object : SimpleAnimatorListener() {
                override fun onAnimationStart(animation: Animator) {
                    circleImageView.alpha = 0f
                    circleImageView.scale = 0f
                }
            })

            addUpdateListener {
                selectedImageView.alpha = animatedFraction
                unselectedImageView.alpha = 1 - animatedFraction

                val scale = animatedValue as Float

                // 先缩小后放大再还原
                selectedImageView.scale = scale
                unselectedImageView.scale = scale

                // 放大时显示点缀图片
                if (animatedFraction > 0.25f) {
                    shiningImageView.scale = animatedFraction * 1.25f - 0.25f
                    shiningImageView.alpha = animatedFraction * 1.25f - 0.25f
                }

                // 红圈从小到大，然后先显示后隐藏
                if (animatedFraction < 0.5f) {
                    circleImageView.alpha = 2 * animatedFraction
                } else {
                    circleImageView.alpha = 2 - animatedFraction * 2
                }
                circleImageView.scale = animatedFraction
            }

            interpolator = AccelerateDecelerateInterpolator()
            duration = 350
            start()
        }
    }

    private fun showUncheckedImage() {
        uncheckedAnimator.run {
            removeAllListeners()
            addUpdateListener({ animation ->
                val animatedFraction = animation.animatedFraction

                // 隐藏选中图片
                selectedImageView.alpha = 1.0f - animatedFraction
                shiningImageView.alpha = 1.0f - animatedFraction

                // 显示未选中图片
                unselectedImageView.alpha = animatedFraction

                // 瞬间缩小然后恢复
                val scale = animatedValue as Float
                selectedImageView.scale = scale
                unselectedImageView.scale = scale
            })

            interpolator = AccelerateDecelerateInterpolator()
            duration = 200
            start()
        }
    }
}
