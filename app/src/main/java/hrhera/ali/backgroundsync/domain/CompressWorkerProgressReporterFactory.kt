package hrhera.ali.backgroundsync.domain

import androidx.work.CoroutineWorker
import dagger.assisted.AssistedFactory
import hrhera.ali.backgroundsync.ui.worker.CompressProgressReporter

@AssistedFactory
interface CompressWorkerProgressReporterFactory {
    fun createCompressProgress(worker: CoroutineWorker): CompressProgressReporter
}