package com.astrash.gesturelab

import com.astrash.gesturelab.motion.gesture.Gesture
import com.astrash.gesturelab.motion.gesture.impl.*
import com.astrash.gesturelab.slider.PopupSlider
import com.astrash.gesturelab.system.SystemActions
import com.astrash.gesturelab.ui.PopupHandler
import com.astrash.gesturelab.ui.view.Selector

private fun Gesture.thenRun(action: () -> Unit): Gesture = RunGesture(action, this)

// TODO - Un-hardcode and convert into configurable data structure
class MainGestures {
    companion object {
        private const val SLIDER_SLOP = 50f
        fun get(
            actions: SystemActions,
            brightnessPopupSlider: PopupSlider,
            volumePopupSlider: PopupSlider,
            mediaPopupSlider: PopupSlider,
            selector: PopupHandler<Selector<() -> Unit>>,
        ): Gesture {
            val controlBrightness = WaitVerticalGesture(
                SLIDER_SLOP, SliderGesture(
                    liftAction = actions.vibrator::click,
                    getPercent = actions.brightness::getPercent,
                    setPercent = brightnessPopupSlider::set,
                    size = 500f,
                )
            )
                .thenRun(actions.vibrator::click)
                .thenRun { brightnessPopupSlider.set(actions.brightness.getPercent()) }

            val controlVolume = run {
                val sliderGesture = SliderGesture(
                    liftAction = actions.vibrator::click,
                    getPercent = actions.media::getVolumePercent,
                    setPercent = volumePopupSlider::set,
                    size = 700f,
                )
                PredicateSelectorGesture(
                    PredicateSelectorGesture.Predicate { _, _, _, _ -> actions.media.getVolumePercent() == 0.0f } to
                            WaitVerticalGesture(250f, sliderGesture),
                    elze = WaitVerticalGesture(50f, sliderGesture)
                )
                    .thenRun(actions.vibrator::click)
                    .thenRun { volumePopupSlider.set(actions.media.getVolumePercent()) }
            }

            val notifications = RunGesture(actions::openNotifications).then(
                DragGesture()
                    .down(
                        300f, RunGesture(actions::openQuickSettings).then(
                            DragGesture()
                                .down(
                                    500f, RunGesture(actions::openSettings).then(
                                        DragGesture()
                                            .up(200f, RunGesture(actions::openAccessibilitySettings))
                                    )
                                )
                                .up(
                                    200f, RunGesture(actions::back).then(
                                        DragGesture()
                                            .up(200f, RunGesture(actions::back))
                                    )
                                )
                        )
                    )
                    .up(
                        300f, RunGesture(actions::back).then(
                            DragGesture()
                                .up(300f, RunGesture(actions::back))
                        )
                    )
            )

            val tapLayer1 = FlingGesture()
                .down(actions::openNotifications)
                .up(actions::openRecents)
                .side(actions::back)
                .otherwise(
                    DragGesture()
                        .side(
                            100f, RunGesture(actions.vibrator::click).then(
                                WaitStopGesture(
                                    2f, DragGesture()
                                        .down(75f, controlBrightness)
                                        .up(75f, controlVolume)
                                )
                            )
                        )
                        .up(
                            100f, RunGesture(actions::openRecents).then(
                                WaitStopGesture(
                                    1f, DragGesture()
                                        .down(100f, RunGesture(actions::home))
                                        .up(100f, RunGesture(actions::openRecents))
                                )
                            )
                        )
                        .down(100f, notifications)
                )

            val tapLayer2 = DragGesture()
                .up(100f, RunGesture(actions.vibrator::click).then(RunGesture(actions.media::next)))
                .down(100f, RunGesture(actions.vibrator::click).then(RunGesture(actions.media::previous)))
                .side(
                    200f,
                    WaitStopGesture(1.5f, SelectorGesture(selector))
                )

            return TapGesture(
                tapCompleteActions = listOf(
                    {},
                    { actions.media.togglePlayPause(); actions.vibrator.doubleClick() }
                ),
                holdCompleteActions = listOf(
                    {},
                    actions.media::openPlayingMediaApp,
                ),
                onHoldActions = listOf(
                    {},
                    actions.vibrator::click,
                ),
                dragStates = listOf(
                    tapLayer1,
                    tapLayer2,
                )
            )
        }
    }
}
