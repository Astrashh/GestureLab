package com.astrash.gesturelab.extension

fun Boolean.applyIfTrue(block: () -> Unit): Boolean = this.also { if (it) block.invoke() }