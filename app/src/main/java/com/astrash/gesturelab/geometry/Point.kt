package com.astrash.gesturelab.geometry

data class Point<T : Number>(val x: T, val y: T) {
    data class Mutable<T : Number>(var x: T, var y: T)
}
