package hrhera.ali.backgroundsync.domain.usecases

import hrhera.ali.backgroundsync.domain.repo.UploadRepository
import javax.inject.Inject

class GetLastUploadedItemUseCase @Inject constructor(
    private val repository: UploadRepository
) {
    suspend operator fun invoke(itemId: String): Int {
        return repository.getLastUploadedItem(itemId)
    }
}