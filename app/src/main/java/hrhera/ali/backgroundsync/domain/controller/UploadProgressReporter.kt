package hrhera.ali.backgroundsync.domain.controller

interface UploadProgressReporter {
    suspend fun report(progress: Int,itemId: String)
}