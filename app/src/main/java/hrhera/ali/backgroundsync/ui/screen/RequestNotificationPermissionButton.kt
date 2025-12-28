package hrhera.ali.backgroundsync.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestNotificationPermissionButton(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
            showDialog=true
        }
    }


    Button(onClick = {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }

        if (permissions.isNotEmpty()) {
            launcher.launch(permissions.toTypedArray())
        } else {
            onPermissionGranted()
        }
    }) {
        Text("Start compress and upload")
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text("Please grant notification permission") },
            text = { Text("To inform you about upload progress") },
            confirmButton = {
                TextButton(onClick = {

                    val perms = mutableListOf<String>()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        perms += Manifest.permission.READ_MEDIA_VIDEO
                        perms += Manifest.permission.POST_NOTIFICATIONS
                    } else {
                        perms += Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    launcher.launch(perms.toTypedArray())
                    showDialog = false

                }) {
                    Text("Okay")
                }
            }
        )
    }
}
