package hrhera.ali.backgroundsync.data.network

import hrhera.ali.backgroundsync.data.dto.UploadRequestBody
import hrhera.ali.backgroundsync.data.dto.UploadResponse

interface UploadService {

    suspend fun uploadPart(
        request: UploadRequestBody
    ): UploadResponse


    suspend fun getLastUploadedItem(itemId: String): UploadResponse

}