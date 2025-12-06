package ru.hqr.tinywms.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EnableBiometricDialog(
    onEnable: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { },
        text = {
//            Text(text = stringResource(R.string.enable_biometric_dialog_title_text))
            Text(text = "enable biometric")
        },
        confirmButton = {
            Button(onClick = {
                onEnable()
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Dismiss")
            }
        }
    )
}