package hrhera.ali.backgroundsync.util.compressers

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import dagger.hilt.android.qualifiers.ApplicationContext
import hrhera.ali.backgroundsync.App
import java.io.File
import javax.inject.Inject

class FfmpegCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaCompressor {

    private var totalDurationSeconds = 0L

    private fun getCommandLine(oFilePath: File, inFilePath: String): String {
        val compressCommandLine = arrayOf(
            "-y",
            "-i", inFilePath,
            "-vcodec", "mpeg4",
            "-crf", "28",
            "-preset", "fast",
            oFilePath.absolutePath
        )

        val safeArray = compressCommandLine.map { part ->
            if (part.contains(" ")) "\"$part\"" else part
        }.toTypedArray()
        return safeArray.joinToString(" ")
    }

    private fun parseProgress(message: String, progressCallback: (Float) -> Unit) {
        val regex = "time=([0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{2})".toRegex()
        val match = regex.find(message)
        if (match != null) {
            val timeStr = match.groupValues[1]
            val seconds = timeStr.split(":").let {
                it[0].toLong() * 3600 +
                        it[1].toLong() * 60 +
                        it[2].toDouble()
            }

            val progress = (seconds / totalDurationSeconds).toFloat()
            progressCallback(progress.coerceIn(0f, 1f))
        }
    }

    private fun getVideoDurationInSeconds(uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val durationMs = retriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull() ?: 0L
        retriever.release()
        return durationMs / 1000
    }

    private fun copyUriToCache(uri: Uri, itemId: String): File {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open URI")
        val cacheDir = File(App.appCacheDir, "temp_videos/$itemId")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val outFile = File(cacheDir, "input_${System.currentTimeMillis()}.mp4")
        input.use { inputStream ->
            outFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return outFile
    }

    override suspend fun compress(
        inputFilePath: String,
        itemId: String,
        progress: ((Float) -> Unit)?
    ): CompressStatus {

        // Support Content URI
        val inputFile = if (inputFilePath.startsWith("content://")) {
            copyUriToCache(Uri.parse(inputFilePath), itemId)
        } else {
            File(inputFilePath)
        }

        totalDurationSeconds = getVideoDurationInSeconds(Uri.fromFile(inputFile))
        val cacheDir = File(App.appCacheDir, "compressed_videos/$itemId")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val outputFile = File(cacheDir, "compressed_${System.currentTimeMillis()}.mp4")

        val compressCommandLine = getCommandLine(outputFile, inputFile.absolutePath)

        FFmpegKitConfig.enableLogCallback { log ->
            parseProgress(log.message) { progress?.invoke(it) }
        }
        val oldSize=inputFile.length() / 1024
        val session = FFmpegKit.execute(compressCommandLine)
        return if (ReturnCode.isSuccess(session.returnCode)) {
            val newSize=outputFile.length() / 1024
            if (inputFile.exists()) inputFile.delete()
            CompressStatus.Success(
                outputFile.absolutePath, "old size=$oldSize kb  new size =$newSize KB"
            )
        } else {
            Log.e("TAG compress", "FFmpeg failed! Logs: ${session.allLogsAsString}")
            CompressStatus.Failed
        }
    }
}
