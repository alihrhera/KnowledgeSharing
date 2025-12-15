package hrhera.ali.backgroundsync.domain.controller

interface UploadStateController {
    fun pause(itemName: String)
    fun resume(itemName: String)
    fun cancel(itemName: String)
    fun isPaused(itemName: String): Boolean
    fun isCanceled(itemName: String): Boolean
}