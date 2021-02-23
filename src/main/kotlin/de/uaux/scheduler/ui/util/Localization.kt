package de.uaux.scheduler.ui.util

import androidx.compose.runtime.Composable
import de.uaux.scheduler.util.LocalizationUtil
import org.koin.androidx.compose.get

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun l(key: String) = get<LocalizationUtil>()[key]