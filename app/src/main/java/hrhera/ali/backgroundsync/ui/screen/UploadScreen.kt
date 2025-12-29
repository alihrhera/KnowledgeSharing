package hrhera.ali.backgroundsync.ui.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import hrhera.ali.backgroundsync.ui.worker.ScreenAction
import hrhera.ali.backgroundsync.ui.worker.ScreenState
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
        val state = workerViewmodel.screenState.collectAsState().value
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Operation name: ${state.opName}")
            LinearProgressIndicator(
                progress = {
                    state.progress / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Progress: ${state.progress} %", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            if(state.size.isNotBlank()){
                Text(text = "Compressing done with ${state.size}", style = MaterialTheme.typography
                    .bodyMedium)
            }
        }
        VideoPicker(
            state,
            onVideoSelected = {
                val filePath = it.toString()
                workerViewmodel.emitAction(
                    ScreenAction.StartUpload(
                        itemId = "1",
                        filePath = filePath
                    )
                )
            },
            emitAction = {
                workerViewmodel.emitAction(it)

            }
        ){
            workerViewmodel.emitAction(ScreenAction.CheckUpload)

        }

    }


}

@Composable
fun VideoPicker(
    state: ScreenState,
    onVideoSelected: (Uri) -> Unit,
    emitAction: (ScreenAction) -> Unit,
    onCloseVideoSelected: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onVideoSelected(it)
        }?:onCloseVideoSelected()
    }
    RequestNotificationPermissionButton(
        onPermissionDenied = {
        }, onPermissionGranted = {
            emitAction(
                ScreenAction.CheckUpload
            )
        })
    if (state.checkUpload) {
        launcher.launch("video/*")
    }
}

