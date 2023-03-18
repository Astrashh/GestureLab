package com.astrash.gesturelab.motion.gesture.impl

import com.astrash.gesturelab.motion.Motion
import com.astrash.gesturelab.motion.gesture.Gesture

class PredicateSelectorGesture(
    private vararg val predicates: Pair<Predicate, Gesture>,
    private val elze: Gesture? = null,
) : Gesture {
    override fun state(motion: Motion, x: Float, y: Float, t: Long): Gesture.State =
        State(motion, predicates.toList(), elze)

    private class State(
        private val motion: Motion,
        private val predicates: List<Pair<Predicate, Gesture>>,
        private val elze: Gesture?
    ) : Gesture.State() {

        override fun update(x: Float, y: Float, t: Long): Gesture.State {
            for ((predicate, builder) in predicates) {
                if (predicate.test(x, y, t, this)) return builder.state(motion, x, y, t)
            }
            return elze?.state(motion, x, y, t) ?: this
        }

    }

    fun interface Predicate {
        fun test(x: Float, y: Float, t: Long, state: Gesture.State): Boolean
    }
}
