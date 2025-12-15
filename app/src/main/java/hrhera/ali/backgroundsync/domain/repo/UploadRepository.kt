package hrhera.ali.backgroundsync.domain.repo

interface UploadRepository {
    suspend fun uploadPart(
        filePath: String,
        itemId: String,
        partIndex: Int
    ): Int

    suspend fun getLastUploadedItem(itemId: String):Int
}