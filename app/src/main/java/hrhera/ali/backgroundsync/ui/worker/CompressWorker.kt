package hrhera.ali.backgroundsync.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import hrhera.ali.backgroundsync.domain.CompressWorkerProgressReporterFactory
import hrhera.ali.backgroundsync.ui.notification.CompressNotificationFactory
import hrhera.ali.backgroundsync.util.COMPRESS_NOTIFICATION_ID
import hrhera.ali.backgroundsync.util.FILE_PATH_KEY
import hrhera.ali.backgroundsync.util.ITEM_ID_KEY
import hrhera.ali.backgroundsync.util.compressers.CompressStatus
import hrhera.ali.backgroundsync.domain.MediaCompressorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltWorker
class CompressWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val progressReporterFactory: CompressWorkerProgressReporterFactory,
    private val mediaCompressorFactory: MediaCompressorFactory
) : CoroutineWorker(context, params) {

    private val itemId = params.inputData.getString(ITEM_ID_KEY) ?: ""
    private val filePath = params.inputData.getString(FILE_PATH_KEY) ?: ""
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            val result = compressStatus()
            when (result) {
                is CompressStatus.Success -> {
                    val data = workDataOf(
                        FILE_PATH_KEY to result.outPutFilePath,
                        ITEM_ID_KEY to itemId,
                        "Size" to result.newSize
                    )
                    return@withContext Result.success(data)
                }
                else -> {
                    return@withContext Result.failure()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.failure()
        }
    }

    private suspend fun compressStatus(): CompressStatus? {
        val compressor = mediaCompressorFactory.create(type = "FFMPEG")
        val result = compressor?.compress(
            inputFilePath = filePath,
            itemId = itemId,
            progress = {
                CoroutineScope(Dispatchers.IO).launch {
                    progressReporterFactory.createCompressProgress(this@CompressWorker).report(
                        progress = (it * 100).toInt(),
                        itemId = itemId,
                        isCanceled = false,
                        isPaused = false
                    )
                }
            }
        )
        return result
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            COMPRESS_NOTIFICATION_ID,
            CompressNotificationFactory.create(
                context = applicationContext,
                text = "Compressing ${inputData.getString(ITEM_ID_KEY)}",
                progress = 0f,
            )
        )
    }


}