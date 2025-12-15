package hrhera.ali.backgroundsync.domain.models

sealed class UploadResult {
    object Success : UploadResult()
    object Retry : UploadResult()
    object Canceled : UploadResult()
}
