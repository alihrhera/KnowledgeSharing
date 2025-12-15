package hrhera.ali.backgroundsync.domain.models

data class ItemFileInfo(
        val originalFilePath: String,
        val chunkSizeInMb: Int,
        val itemId: String,
        val folderName: String,
        val parts: Map<Int, String>,
        val createdAt: Long=System.currentTimeMillis(),
    )