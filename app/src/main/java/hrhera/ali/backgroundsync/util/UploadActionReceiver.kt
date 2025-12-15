package hrhera.ali.backgroundsync.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import hrhera.ali.backgroundsync.domain.controller.UploadStateController
import javax.inject.Inject

@AndroidEntryPoint
class UploadActionReceiver  :
    BroadcastReceiver() {
    @Inject
    lateinit var uploadStateController: UploadStateController

    override fun onReceive(context: Context, intent: Intent) {
        val itemName = intent.getStringExtra(ITEM_ID_KEY) ?: return
        when (intent.action) {
            ACTION_PAUSE -> uploadStateController.pause(itemName)
            ACTION_RESUME ->uploadStateController.resume(itemName)
            ACTION_CANCEL -> { uploadStateController.cancel(itemName)
                WorkManager.getInstance(context)
                    .cancelUniqueWork(itemName)
            }
        }
    }
}
