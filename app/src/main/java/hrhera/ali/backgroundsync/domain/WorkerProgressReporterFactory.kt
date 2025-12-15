package hrhera.ali.backgroundsync.domain

import androidx.work.CoroutineWorker
import dagger.assisted.AssistedFactory
import hrhera.ali.backgroundsync.ui.worker.WorkerProgressReporter

@AssistedFactory
interface WorkerProgressReporterFactory {
    fun create(worker: CoroutineWorker): WorkerProgressReporter
}