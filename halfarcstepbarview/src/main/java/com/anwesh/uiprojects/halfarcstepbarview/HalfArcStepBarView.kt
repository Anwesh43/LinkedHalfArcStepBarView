package com.anwesh.uiprojects.halfarcstepbarview

/**
 * Created by anweshmishra on 14/07/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
import android.content.Context

val nodes : Int = 5
val bars : Int = 4
val scGap : Float = 0.05f
val scDiv : Double = 0.51
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#283593")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.scaleFactor() : Float = Math.floor(this / scDiv).toFloat()
fun Float.mirrorValue(a : Int, b : Int) : Float {
    val k : Float = scaleFactor()
    return (1 - k) * a.inverse() + k * b.inverse()
}
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.updateValue(dir : Float, a : Int, b : Int) : Float = mirrorValue(a, b) * dir * scGap

fun Canvas.drawHalfArc(scale : Float, size : Float, paint : Paint) {
    drawArc(RectF(-size, -size, size, size), 0f, 180f * scale, true, paint)
}

fun Canvas.drawBar(i : Int, sc : Float, size : Float, paint : Paint) {
    val wBar : Float = (2 * size) / (2 * bars + 1)
    val sci : Float = sc.divideScale(i, bars)
    save()
    translate(wBar * (i + 1), 0f)
    drawRect(RectF(-wBar / 2, -size * sci, wBar / 2, 0f), paint)
    restore()
}

fun Canvas.drawBars(size : Float, sc : Float, paint : Paint) {
    for (j in 0..(bars - 1)) {
        drawBar(j, sc, size, paint)
    }
}

fun Canvas.drawHASBNode(i : Int, scale : Float, paint : Paint) {
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawBars(size, sc2, paint)
    drawHalfArc(sc1, size, paint)
    restore()
}

class HalfArcStepBarView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}