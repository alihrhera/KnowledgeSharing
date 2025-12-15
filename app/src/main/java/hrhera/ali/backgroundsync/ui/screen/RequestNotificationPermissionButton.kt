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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    Button(
        onClick = {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                onPermissionGranted()
                return@Button
            }
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onPermissionGranted()
                }

                else -> showDialog=true
            }
        }
    ) {
        Text("Start Upload")
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text("Please grant notification permission") },
            text = { Text("To inform you about upload progress") },
            confirmButton = {
                TextButton(onClick = {
                    permissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                    showDialog = false

                }) {
                    Text("Okay")
                }
            }
        )
    }
}
