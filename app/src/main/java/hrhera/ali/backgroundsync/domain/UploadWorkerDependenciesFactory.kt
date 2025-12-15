package hrhera.ali.backgroundsync.domain

import androidx.work.CoroutineWorker
import hrhera.ali.backgroundsync.domain.controller.UploadProgressReporter
import hrhera.ali.backgroundsync.domain.usecases.UploadPartsUseCase

interface UploadWorkerDependenciesFactory {
    fun createUseCase(): UploadPartsUseCase
    fun createReporter(worker: CoroutineWorker): UploadProgressReporter
}