package hrhera.ali.backgroundsync.domain.usecases

import hrhera.ali.backgroundsync.domain.controller.ProgressReporter
import hrhera.ali.backgroundsync.domain.models.ItemFileInfo
import hrhera.ali.backgroundsync.util.UPLOAD_COOLDOWN
import hrhera.ali.backgroundsync.domain.repo.UploadRepository
import hrhera.ali.backgroundsync.domain.controller.UploadStateController
import hrhera.ali.backgroundsync.domain.models.UploadResult
import kotlinx.coroutines.delay
import javax.inject.Inject

class UploadPartsUseCase @Inject constructor(
    private val repository: UploadRepository,
    private val stateController: UploadStateController,
) {
    private val lastUploadedItemUseCase: GetLastUploadedItemUseCase = GetLastUploadedItemUseCase(repository)

    suspend operator fun invoke(
        item: ItemFileInfo,
        progressReporter: ProgressReporter,
    ): UploadResult {

        val lastUploadedPart = lastUploadedItemUseCase(item.itemId)
        return execute(lastUploadedPart, item, progressReporter)
    }

    private suspend fun execute(
        lastUploadedPart: Int,
        item: ItemFileInfo,
        progressReporter: ProgressReporter,
        ): UploadResult {
        var lastUploadedPart = lastUploadedPart
        while (lastUploadedPart < item.parts.size - 1) {

            if (stateController.isCanceled(item.itemId)){
                progressReporter.report(
                    calcProgress(lastUploadedPart, item),
                    itemId = item.itemId,
                    isCanceled = true,
                    isPaused = false
                )
                return UploadResult.Canceled
            }

            if (stateController.isPaused(item.itemId)) {
                progressReporter.report(
                    calcProgress(lastUploadedPart, item),
                    itemId = item.itemId,
                    isCanceled = false,
                    isPaused = stateController.isPaused(item.itemId)
                )
                return UploadResult.Retry
            }
            val progress = calcProgress(lastUploadedPart, item)
            progressReporter.report(
                progress,
                itemId = item.itemId,
                isCanceled = false,
                isPaused = stateController.isPaused(item.itemId)
            )
            val nextPartIndex = lastUploadedPart + 1
            val filePath = item.parts[nextPartIndex] ?: break
            try {
                lastUploadedPart = repository.uploadPart(
                    filePath = filePath,
                    itemId = item.itemId,
                    partIndex = nextPartIndex
                )
                val progress = calcProgress(lastUploadedPart, item)
                progressReporter.report(
                    progress,
                    itemId = item.itemId,
                    isCanceled = false,
                    isPaused = stateController.isPaused(item.itemId)
                )

                delay(UPLOAD_COOLDOWN)
            } catch (_: Exception) {
                return UploadResult.Retry
            }
        }
        return UploadResult.Success
    }

    private fun calcProgress(lastUploadedPart: Int, item: ItemFileInfo): Int {
        val progress =
            ((lastUploadedPart + 1) * 100) / item.parts.size
        return progress
    }


}