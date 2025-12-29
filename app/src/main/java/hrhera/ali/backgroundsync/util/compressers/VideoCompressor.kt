package hrhera.ali.backgroundsync.util.compressers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.StorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.VideoResizer
import dagger.hilt.android.qualifiers.ApplicationContext
import hrhera.ali.backgroundsync.App
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject

class FullyCustomizedStorageConfiguration(
    private val itemId: String
) : StorageConfiguration {
    private val fileName = "compressed_${System.currentTimeMillis()}"
    override fun createFileToSave(
        context: Context,
        videoFile: File,
        fileName: String,
        shouldSave: Boolean,
    ): File {
        val cacheDir = File(App.appCacheDir, "compressed_videos/$itemId")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val outputFile = File(cacheDir, "$fileName.mp4")
        return outputFile
    }

    fun name(): String = fileName
}

class VideoCompressorLib @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaCompressor {

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

        val inputFile = if (inputFilePath.startsWith("content://")) {
            copyUriToCache(inputFilePath.toUri(), itemId)
        } else {
            File(inputFilePath)
        }

        return compressVideo(
            context = context,
            inputFile = inputFile,
            itemId = itemId,
            progress = progress
        )
    }


    suspend fun compressVideo(
        context: Context,
        inputFile: File,
        itemId: String,
        progress: ((Float) -> Unit)? = null
    ): CompressStatus = suspendCancellableCoroutine { continuation ->
        val outputFile = FullyCustomizedStorageConfiguration(itemId)
        val  oldSize= "${ inputFile.length() / 1024 } kb"
        VideoCompressor.start(
            context = context,
            uris = listOf(inputFile.toUri()),
            isStreamable = false,
            storageConfiguration = outputFile,
            configureWith = Configuration(
                videoNames = listOf(outputFile.name()),
                quality = VideoQuality.MEDIUM,
                isMinBitrateCheckEnabled = true,
                videoBitrateInMbps = 2,
                disableAudio = false,
                resizer = VideoResizer.matchSize(720.0, 1280.0, true)
            ),
            listener = object : CompressionListener {

                override fun onProgress(index: Int, percent: Float) {
                    progress?.invoke(percent/100)
                }

                override fun onStart(index: Int) {}

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    continuation.resume(
                        CompressStatus.Success(
                            path ?: "",
                            "old size= $oldSize new size = ${size / 1024}"
                        )
                    ) { cause, _, _ -> }
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    Log.w("TAG", "onFailure: $failureMessage")
                    continuation.resume(CompressStatus.Failed) { cause, _, _ -> }
                }

                override fun onCancelled(index: Int) {
                    continuation.resume(CompressStatus.Failed) { cause, _, _ -> }
                }
            }
        )
    }


}
