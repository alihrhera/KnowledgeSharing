package hrhera.ali.backgroundsync.ui.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import hrhera.ali.backgroundsync.R
import hrhera.ali.backgroundsync.util.ACTION_CANCEL
import hrhera.ali.backgroundsync.util.ACTION_PAUSE
import hrhera.ali.backgroundsync.util.ACTION_RESUME
import hrhera.ali.backgroundsync.util.ITEM_ID_KEY
import hrhera.ali.backgroundsync.util.UPLOADING_CHANNEL_ID
import hrhera.ali.backgroundsync.util.UploadActionReceiver

object UploadNotificationFactory {

    fun create(
        context: Context,
        text: String,
        itemId: String,
        progress: Float,
        isPaused: Boolean = false,
        isCanceled: Boolean = false
    ): Notification {

        val notificationCompat = NotificationCompat.Builder(context, UPLOADING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("File Upload")
            .setContentText(text)
            .setOngoing(progress < 100f)
            .setProgress(100, progress.toInt(), false)

        setupActions(
            progress,
            context,
            itemId,
            notificationCompat,
            isPaused,
            isCanceled
        )

        return notificationCompat.build()
    }

    private fun setupActions(
        progress: Float,
        context: Context,
        itemId: String,
        notificationCompat: NotificationCompat.Builder,
        isPaused: Boolean,
        isCanceled: Boolean,
    ) {
        if (!isCanceled && progress < 100f) {
            val callUploadActionReceiver = Intent(context, UploadActionReceiver::class.java)
            val pausePending = pendingIntent(context, ACTION_PAUSE, callUploadActionReceiver, itemId, 0)
            val resumePending = pendingIntent(context, ACTION_RESUME, callUploadActionReceiver, itemId, 1)
            val cancelPending = pendingIntent(context, ACTION_CANCEL, callUploadActionReceiver, itemId, 2)
            if (isPaused) {
                notificationCompat.addAction(R.drawable.ic_play, "Resume", resumePending)
            } else {
                notificationCompat.addAction(R.drawable.ic_pause, "Pause", pausePending)
            }
            notificationCompat.addAction(R.drawable.ic_cancel, "Cancel", cancelPending)
        }
    }

    private fun pendingIntent(
        context: Context,
        actionValue: String,
        callUploadActionReceiver: Intent,
        itemId: String,
        requestCode: Int
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context, requestCode, callUploadActionReceiver.also {
                it.action = actionValue
                it.putExtra(ITEM_ID_KEY, itemId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
