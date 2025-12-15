package hrhera.ali.backgroundsync.data

import androidx.work.CoroutineWorker
import hrhera.ali.backgroundsync.domain.WorkerProgressReporterFactory
import hrhera.ali.backgroundsync.domain.UploadWorkerDependenciesFactory
import hrhera.ali.backgroundsync.domain.controller.UploadProgressReporter
import hrhera.ali.backgroundsync.domain.controller.UploadStateController
import hrhera.ali.backgroundsync.domain.repo.UploadRepository
import hrhera.ali.backgroundsync.domain.usecases.UploadPartsUseCase
import javax.inject.Inject

class UploadWorkerDependenciesFactoryImpl @Inject constructor(
    private val repository: UploadRepository,
    private val stateController: UploadStateController,
    private val reporterFactory: WorkerProgressReporterFactory
) : UploadWorkerDependenciesFactory {
    override fun createUseCase(): UploadPartsUseCase {
        return  UploadPartsUseCase(repository, stateController,)
    }
    override fun createReporter(worker: CoroutineWorker): UploadProgressReporter {
        return reporterFactory.create(worker)
    }
}