package com.astrash.gesturelab.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

fun clamp(x: Float, lower: Float, upper: Float) = max(lower, min(x, upper))

fun pow2(a: Float) = a * a

fun distance(x1: Float, y1: Float, x2: Float, y2: Float) = sqrt(pow2(x1 - x2) + pow2(y1 - y2))

fun magnitude(x: Float, y: Float) = sqrt(pow2(x) + pow2(y))

fun magnitude(vector: Pair<Float, Float>) = magnitude(vector.first, vector.second)

fun radToDeg(rad: Float) = rad * 180 / Math.PI

fun lerp(x: Float, x1: Float, y1: Float, x2: Float, y2: Float) =
    ((y1 - y2) * (x - x1)) / (x1 - x2) + y1
