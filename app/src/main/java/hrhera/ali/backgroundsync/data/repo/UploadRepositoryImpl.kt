package hrhera.ali.backgroundsync.data.repo

import hrhera.ali.backgroundsync.data.dto.UploadRequestBody
import hrhera.ali.backgroundsync.data.network.UploadService
import hrhera.ali.backgroundsync.domain.repo.UploadRepository
import javax.inject.Inject

class UploadRepositoryImpl @Inject constructor(private  val api: UploadService): UploadRepository {
    override suspend fun uploadPart(
        filePath: String,
        itemId: String,
        partIndex: Int
    ): Int {
        return api.uploadPart(
            request = UploadRequestBody(
                part = filePath,
                itemId = itemId,
                partIndex = partIndex
            )
        ).lastUploadedId
    }

    override suspend fun getLastUploadedItem(itemId: String): Int {
        return api.getLastUploadedItem(itemId).lastUploadedId
    }

}