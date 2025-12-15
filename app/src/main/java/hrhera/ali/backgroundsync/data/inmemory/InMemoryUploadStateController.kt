package hrhera.ali.backgroundsync.data.inmemory

import hrhera.ali.backgroundsync.domain.controller.UploadStateController
import javax.inject.Inject

class InMemoryUploadStateController @Inject constructor() : UploadStateController {

    private val pausedItems = mutableSetOf<String>()
    private val canceledItems = mutableSetOf<String>()

    override fun pause(itemName: String) {
        pausedItems.add(itemName)
    }

    override fun resume(itemName: String) {
        pausedItems.remove(itemName)
    }

    override fun cancel(itemName: String) {
        canceledItems.add(itemName)
    }

    override fun isPaused(itemName: String) = pausedItems.contains(itemName)
    override fun isCanceled(itemName: String) = canceledItems.contains(itemName)
}
