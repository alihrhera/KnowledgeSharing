package hrhera.ali.backgroundsync.domain

import hrhera.ali.backgroundsync.util.CompressType
import hrhera.ali.backgroundsync.util.compressers.FfmpegCompressor
import hrhera.ali.backgroundsync.util.compressers.MediaCompressor
import hrhera.ali.backgroundsync.util.compressers.VideoCompressorLib
import javax.inject.Inject

class MediaCompressorFactory @Inject constructor(
    private val ffmpegCompressor: FfmpegCompressor,
    private val videoCompressor: VideoCompressorLib
) {
    fun create(type: String): MediaCompressor? {
        return when (type) {
            CompressType.FFMPEG.name -> ffmpegCompressor
            CompressType.VideoCompressor.name -> videoCompressor
            else -> {
                throw UnsupportedOperationException("Unsupported operation")
            }
        }
    }
}