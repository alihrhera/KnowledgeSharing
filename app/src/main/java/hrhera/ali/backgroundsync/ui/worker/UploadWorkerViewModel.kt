package hrhera.ali.backgroundsync.ui.worker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hrhera.ali.backgroundsync.util.COMPRESS_TYPE_KEY
import hrhera.ali.backgroundsync.util.CompressType
import hrhera.ali.backgroundsync.util.FILE_PATH_KEY
import hrhera.ali.backgroundsync.util.ITEM_ID_KEY
import hrhera.ali.backgroundsync.util.PROGRESS_KEY
import hrhera.ali.backgroundsync.util.SIZE_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ScreenState(
    val progress: Float = 0f,
    val opName: String = "",
    val checkUpload: Boolean = false,
    val size: String = ""
)

sealed class ScreenAction {
    data class StartUpload(val itemId: String, val filePath: String = "") : ScreenAction()
    data object CheckUpload : ScreenAction()
    data object NoSelection : ScreenAction()

}

@HiltViewModel
class UploadWorkerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    val screenState = MutableStateFlow(ScreenState())
    fun emitAction(action: ScreenAction) {
        when (action) {
            is ScreenAction.CheckUpload -> screenState.value = ScreenState(checkUpload = true)
            is ScreenAction.StartUpload -> startComperes(
                itemId = action.itemId,
                filePath = action.filePath,
            )

            is ScreenAction.NoSelection -> screenState.value = ScreenState(checkUpload = false)
        }
    }

    private fun startComperes(itemId: String, filePath: String = "") {
        screenState.value = ScreenState(progress = 0f, opName = "Compressing")
        val request = OneTimeWorkRequestBuilder<CompressWorker>()
            .setInputData(
                workDataOf(
                    ITEM_ID_KEY to itemId,
                    FILE_PATH_KEY to filePath,
                    COMPRESS_TYPE_KEY to CompressType.VideoCompressor.name
                )
            )
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            "Compress_$itemId",
            ExistingWorkPolicy.REPLACE,
            request
        )
        workManager.getWorkInfoByIdLiveData(request.id)
            .observeForever { workInfo ->
                workInfo?.let {
                    if (it.state.isFinished) {
                        screenState.value = screenState.value.copy(progress = 100f)
                        val itemId = it.outputData.getString(ITEM_ID_KEY)
                        val filePath = it.outputData.getString(FILE_PATH_KEY)
                        val size = it.outputData.getString(SIZE_KEY) ?: ""
                        if (itemId.isNullOrBlank() || filePath.isNullOrBlank()) return@let
                        startUpload(itemId, filePath, size)
                    } else {
                        val prog = it.progress.getInt(PROGRESS_KEY, 0)
                        screenState.value = screenState.value.copy(progress = prog.toFloat())
                    }
                }
            }
    }

    private fun startUpload(itemId: String, filePath: String = "", size: String) {
        screenState.value = ScreenState(progress = 0f, opName = "Uploading", size = size)

        val request = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(
                workDataOf(
                    ITEM_ID_KEY to itemId,
                    FILE_PATH_KEY to filePath,
                )
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                30,
                TimeUnit.SECONDS
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            "Upload_$itemId",
            ExistingWorkPolicy.REPLACE,
            request
        )
        workManager.getWorkInfoByIdLiveData(request.id)
            .observeForever { workInfo ->
                workInfo?.let {
                    if (it.state.isFinished) {
                        screenState.value = screenState.value.copy(progress = 100f)

                    } else {
                        val prog = it.progress.getInt(PROGRESS_KEY, 0)
                        screenState.value = screenState.value.copy(progress = prog.toFloat())
                    }
                }
            }
    }


}

