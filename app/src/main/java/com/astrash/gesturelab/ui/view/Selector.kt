package com.astrash.gesturelab.ui.view

import android.content.Context
import android.graphics.*
import android.view.View
import com.astrash.gesturelab.extension.screenCoordsToViewCoords
import com.astrash.gesturelab.geometry.Point
import com.astrash.gesturelab.util.distance
import com.astrash.gesturelab.util.lerp
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max

class Selector<T>(
    context: Context,
    private var origin: Point<Float>,
    private val selections: List<Selection<T>>
) : View(context) {

    private val pointer: Point.Mutable<Float> = run {
        val (x, y) = screenCoordsToViewCoords(origin.x, origin.y)
        Point.Mutable(x, y)
    }

    fun setCenter(screenX: Float, screenY: Float) {
        val (x, y) = screenCoordsToViewCoords(screenX, screenY)
        origin = Point(x, y)
    }

    fun setPosition(screenX: Float, screenY: Float) {
        val (localX, localY) = screenCoordsToViewCoords(screenX, screenY)
        pointer.apply {
            x = localX
            y = localY
        }
        invalidate()
    }

    fun select(): T? {
        return selections
            .stream()
            // Sort by distance to pointer
            .map { it to distance(origin.x + it.pos.x, origin.y + it.pos.y, pointer.x, pointer.y,) }
            .sorted { o1, o2 -> o1.second.compareTo(o2.second) }
            // Get closest to pointer
            .findFirst()
            // Check if distance is within selection threshold
            .filter { it.second < 200f }
            .map { it.first }
            // If a valid selection has been found
            .getOrNull()
            ?.selectReturn
    }

    private val smallRadius = 100f
    private val largeRadius = 150f

    private val rect = Rect()
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG);

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            for (selection in selections) {
                val distance = distance(origin.x + selection.pos.x, origin.y + selection.pos.y, pointer.x, pointer.y)
                val radius = max(lerp(distance, 0f, largeRadius, 800f, smallRadius), smallRadius)
                val (x, y) = origin.x + selection.pos.x to origin.y + selection.pos.y
                rect.set((x-radius).toInt(), (y-radius).toInt(), (x+radius).toInt(), (y+radius).toInt())
                drawBitmap(selection.bitmap, null, rect, paint)
            }
        }
    }

    data class Selection<T>(val selectReturn: T, val bitmap: Bitmap, val pos: Point<Float>)
}
