package hrhera.ali.backgroundsync.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import hrhera.ali.backgroundsync.data.UploadWorkerDependenciesFactoryImpl
import hrhera.ali.backgroundsync.domain.models.ItemFileInfo
import hrhera.ali.backgroundsync.domain.models.UploadResult
import hrhera.ali.backgroundsync.domain.usecases.UploadPartsUseCase
import hrhera.ali.backgroundsync.ui.notification.UploadNotificationFactory
import hrhera.ali.backgroundsync.util.CHUNK_SIZE_IN_MB
import hrhera.ali.backgroundsync.util.FILE_PATH_KEY
import hrhera.ali.backgroundsync.util.FileSeparatorUtil
import hrhera.ali.backgroundsync.util.ITEM_ID_KEY
import hrhera.ali.backgroundsync.util.UPLOAD_NOTIFICATION_ID

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val progressReporterFactory: UploadWorkerDependenciesFactoryImpl
) : CoroutineWorker(context, params) {

    private val itemId = params.inputData.getString(ITEM_ID_KEY) ?: ""
    private val chunkSize = CHUNK_SIZE_IN_MB
    private val filePath = params.inputData.getString(FILE_PATH_KEY) ?: ""

    private val item: ItemFileInfo
        get() = FileSeparatorUtil.splitFileToChach(
            context = context,
            chunkSize = chunkSize,
            inputFilePath = filePath,
            itemId = itemId
        )

   /* override suspend fun doWork(): Result {
        val reporter = progressReporterFactory.createReporter(this)
        return when (useCase(item, reporter)) {
            UploadResult.Success -> Result.success()
            UploadResult.Retry -> Result.retry()
            UploadResult.Canceled -> Result.failure()
        }
    }*/
   override suspend fun doWork(): Result {
       Log.w("TAG===========>", "doWork: start", )
       return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            UPLOAD_NOTIFICATION_ID,
            UploadNotificationFactory.create(
                applicationContext,
                "Uploading ${inputData.getString(ITEM_ID_KEY)}",
                ITEM_ID_KEY
            )
        )
    }
}