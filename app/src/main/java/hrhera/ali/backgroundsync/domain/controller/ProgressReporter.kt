package hrhera.ali.backgroundsync.domain.controller

interface ProgressReporter {
    suspend fun report(
        progress: Int,
        itemId: String,
        isPaused: Boolean,
        isCanceled: Boolean
    )
}