package hrhera.ali.backgroundsync.domain

import androidx.work.CoroutineWorker
import dagger.assisted.AssistedFactory
import hrhera.ali.backgroundsync.ui.worker.UploadingProgressReporter

@AssistedFactory
interface WorkerProgressReporterFactory {
    fun createUploadProgress(worker: CoroutineWorker): UploadingProgressReporter
}