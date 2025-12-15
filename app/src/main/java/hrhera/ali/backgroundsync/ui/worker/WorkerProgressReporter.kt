package hrhera.ali.backgroundsync.ui.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import hrhera.ali.backgroundsync.ui.notification.UploadNotificationFactory
import hrhera.ali.backgroundsync.util.UPLOAD_NOTIFICATION_ID
import hrhera.ali.backgroundsync.domain.controller.UploadProgressReporter
import javax.inject.Inject

class WorkerProgressReporter @AssistedInject constructor(
   @Assisted private val worker: CoroutineWorker
) : UploadProgressReporter {

    override suspend fun report(progress: Int,itemId: String) {
        worker.setProgress(
            workDataOf("progress" to progress)
        )
        val notification = UploadNotificationFactory.create(
            worker.applicationContext,
            "Uploading $progress%",
            itemId

        )
        val manager = worker.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(UPLOAD_NOTIFICATION_ID, notification)
    }
}