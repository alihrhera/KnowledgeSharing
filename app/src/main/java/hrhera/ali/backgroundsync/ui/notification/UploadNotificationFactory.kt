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
        itemId: String
    ): Notification {
        val callUploadActionReceiver = Intent(context, UploadActionReceiver::class.java)

        val pausePending = pendingIntent(context, ACTION_PAUSE, callUploadActionReceiver, itemId, 0)
        val resumePending = pendingIntent(context, ACTION_RESUME, callUploadActionReceiver, itemId, 1)
        val cancelPending = pendingIntent(context, ACTION_CANCEL, callUploadActionReceiver, itemId, 2)

        return NotificationCompat.Builder(context, UPLOADING_CHANNEL_ID)
            .setContentTitle("File Upload")
            .setContentText(text)
            .setOngoing(true)
            .addAction(R.drawable.ic_pause, "Pause", pausePending)
            .addAction(R.drawable.ic_play, "Resume", resumePending)
            .addAction(R.drawable.ic_cancel, "Cancel", cancelPending)
            .build()
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
