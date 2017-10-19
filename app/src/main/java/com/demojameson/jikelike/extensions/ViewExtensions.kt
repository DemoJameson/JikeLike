package com.demojameson.jikelike.extensions

import android.view.View

/**
 * author : DemoJameson
 * time   : 2017/10/19
 * desc   : 为 View 添加扩展方法
 */

var View.scale:Float
  get() {
    return scaleX.coerceAtMost(scaleY)
  }
  set(value) {
    scaleX = value
    scaleY = value
  }

fun View.layout(l:Float, t:Float, r:Float, b:Float) {
  layout(l.toInt(), t.toInt(), r.toInt(), b.toInt())
}