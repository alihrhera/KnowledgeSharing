package hrhera.ali.backgroundsync.ui.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import hrhera.ali.backgroundsync.domain.controller.ProgressReporter
import hrhera.ali.backgroundsync.ui.notification.CompressNotificationFactory
import hrhera.ali.backgroundsync.util.COMPRESS_NOTIFICATION_ID
import hrhera.ali.backgroundsync.util.PROGRESS_KEY

class CompressProgressReporter @AssistedInject constructor(
    @Assisted private val worker: CoroutineWorker
) : ProgressReporter {

    override suspend fun report(
        progress: Int, itemId: String,
        isPaused: Boolean, isCanceled: Boolean
    ) {
        worker.setProgress(
            workDataOf(PROGRESS_KEY to progress)
        )
        val notification = CompressNotificationFactory.create(
            worker.applicationContext,
            if (progress < 100)
                "Compressing $progress%"
            else "Compressing Complete and upload will start",
            progress.toFloat(),
        )
        val manager =
            worker.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(COMPRESS_NOTIFICATION_ID, notification)
    }
}