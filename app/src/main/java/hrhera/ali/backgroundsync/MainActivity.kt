package hrhera.ali.backgroundsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import hrhera.ali.backgroundsync.ui.screen.UploadScreen
import hrhera.ali.backgroundsync.ui.theme.BackGroundSyncTheme
import hrhera.ali.backgroundsync.ui.worker.UploadWorker
import hrhera.ali.backgroundsync.util.FILE_PATH_KEY
import hrhera.ali.backgroundsync.util.ITEM_ID_KEY
import hrhera.ali.backgroundsync.util.UploadActionReceiver
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BackGroundSyncTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UploadScreen(innerPadding)
                }
            }
        }

    }
}
