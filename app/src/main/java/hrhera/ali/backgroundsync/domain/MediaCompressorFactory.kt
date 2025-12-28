package hrhera.ali.backgroundsync.domain

import hrhera.ali.backgroundsync.util.compressers.FfmpegCompressor
import hrhera.ali.backgroundsync.util.compressers.MediaCompressor
import javax.inject.Inject

class MediaCompressorFactory @Inject constructor(
    private val ffmpegCompressor: FfmpegCompressor
) {
    fun create(type: String): MediaCompressor? {
        return when (type) {
            "FFMPEG" -> ffmpegCompressor
            else -> {
                throw UnsupportedOperationException("Unsupported operation")
            }
        }
    }
}