package de.uaux.scheduler.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import de.uaux.scheduler.ui.model.DialogState

class DialogViewModel {
    val dialogState: MutableState<DialogState> = mutableStateOf(DialogState.Closed)

    fun openDialog(state: DialogState) {
        dialogState.value = state
    }
}