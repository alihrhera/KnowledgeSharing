package hrhera.ali.backgroundsync.util.compressers

interface MediaCompressor {
    suspend fun compress(
        inputFilePath: String,
        itemId: String,
        progress: ((Float) -> Unit)? = null
    ): CompressStatus

}