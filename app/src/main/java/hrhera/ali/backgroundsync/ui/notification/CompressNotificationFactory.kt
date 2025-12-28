package hrhera.ali.backgroundsync.ui.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import hrhera.ali.backgroundsync.R
import hrhera.ali.backgroundsync.util.UPLOADING_CHANNEL_ID

object CompressNotificationFactory {
    fun create(
        context: Context,
        text: String,
        progress: Float,
    ): Notification {
        val notificationCompat = NotificationCompat.Builder(context, UPLOADING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("File Compress")
            .setContentText(text)
            .setOngoing(progress < 100f)
            .setProgress(100, progress.toInt(), false)
        return notificationCompat.build()
    }
}
