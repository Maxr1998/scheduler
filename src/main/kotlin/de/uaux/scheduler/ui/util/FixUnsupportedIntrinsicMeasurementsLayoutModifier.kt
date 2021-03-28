package de.uaux.scheduler.ui.util

import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

/**
 * Workaround for SubcomposeLayouts not supporting intrinsic measurements.
 *
 * Overrides <min/max>Intrinsic<Width/Height> to return static values instead of crashing with an error message.
 * Returning those values is fine since the layouts bounds are already constrained by the parent view.
 *
 * @see androidx.compose.ui.node.LayoutNode.NoIntrinsicsMeasurePolicy
 */
object FixUnsupportedIntrinsicMeasurementsLayoutModifier : LayoutModifier {
    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measurable.measure(constraints.copy(minHeight = 0, maxHeight = /* MaxNonFocusMask */ 0x1FFF))
        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int = 0
    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int = Int.MAX_VALUE
    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int): Int = 0
    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int): Int = Int.MAX_VALUE
}