package hrhera.ali.backgroundsync.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import hrhera.ali.backgroundsync.ui.worker.UploadWorkerViewModel

@Composable
fun UploadScreen(innerPadding: PaddingValues, workerViewmodel: UploadWorkerViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                innerPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val progress by workerViewmodel.progress.collectAsState()
        Column(modifier = Modifier.padding(16.dp)) {
            LinearProgressIndicator(
                progress = {
                    progress / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Progress: $progress%", style = MaterialTheme.typography.bodyMedium)
        }

        RequestNotificationPermissionButton(
            onPermissionDenied = {
            }, onPermissionGranted = {
                workerViewmodel.startUpload(
                    "item1",
                    workerViewmodel.dummyFileWithSize().absolutePath
                )
            })


    }
}

