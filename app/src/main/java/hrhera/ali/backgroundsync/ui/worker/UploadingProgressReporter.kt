package hrhera.ali.backgroundsync.ui.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import hrhera.ali.backgroundsync.ui.notification.UploadNotificationFactory
import hrhera.ali.backgroundsync.util.UPLOAD_NOTIFICATION_ID
import hrhera.ali.backgroundsync.domain.controller.ProgressReporter
import hrhera.ali.backgroundsync.util.PROGRESS_KEY

class UploadingProgressReporter @AssistedInject constructor(
    @Assisted private val worker: CoroutineWorker
) : ProgressReporter {

    override suspend fun report(progress: Int, itemId: String,
                                isPaused: Boolean, isCanceled: Boolean) {
        worker.setProgress(
            workDataOf(PROGRESS_KEY to progress)
        )
        val notification = UploadNotificationFactory.create(
            worker.applicationContext,
            if (progress < 100)
                "Uploading $progress%"
            else "Uploading Complete",
            itemId,
            progress.toFloat(),
            isPaused = isPaused,
            isCanceled = isCanceled
        )
        if (isCanceled)
            WorkManager.getInstance(worker.applicationContext)
                .cancelUniqueWork(itemId)

        val manager =
            worker.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(UPLOAD_NOTIFICATION_ID, notification)
    }
}