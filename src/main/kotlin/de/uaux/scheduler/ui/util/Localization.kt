package de.uaux.scheduler.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticAmbientOf
import de.uaux.scheduler.util.LocalizationUtil

val AmbientLocalization = staticAmbientOf<LocalizationUtil>()

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun l(key: String) = AmbientLocalization.current[key]