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

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scale.updateValue(dir, 1, bars)
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class HASBNode(var i : Int, val state : State = State()) {

        private var next : HASBNode? = null
        private var prev : HASBNode? = null

        init {

        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = HASBNode(i + 1)
                next?.prev = this

            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawHASBNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : HASBNode {
            var curr : HASBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class HalfArcStepBar(var i : Int) {

        private val root : HASBNode = HASBNode(0)
        private var curr : HASBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : HalfArcStepBarView) {

        private val animator : Animator = Animator(view)
        private val hasb : HalfArcStepBar = HalfArcStepBar(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            hasb.draw(canvas, paint)
            animator.animate {
                hasb.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            hasb.startUpdating {
                animator.start()
            }
        }
    }
}