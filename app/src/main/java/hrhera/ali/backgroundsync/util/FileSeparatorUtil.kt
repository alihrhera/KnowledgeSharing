package hrhera.ali.backgroundsync.util

import android.content.Context
import com.google.gson.Gson
import hrhera.ali.backgroundsync.domain.models.ItemFileInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


object FileSeparatorUtil {


    fun splitFileToChach(
        context: Context,
        inputFilePath: String,
        itemId: String,
        chunkSize: Int
    ): ItemFileInfo {
        val inputFile = File(inputFilePath)
        require(inputFile.exists()){ "Input file does not exist" }
        require(chunkSize > 0) { "chunkSize must be greater than zero" }
        require(inputFile.length()>0){ "Input file is empty" }
        require(itemId.isNotBlank()){ "itemId must not be blank" }
        val itemCacheDir = File(context.cacheDir, itemId)
        val jsonFile = File(itemCacheDir, "info.json")
        val result: ItemFileInfo? =
            itemFileInfo(itemCacheDir, jsonFile, inputFile, chunkSize)

        if (result != null) {
            return result
        }

        val buffer = ByteArray(chunkSizeInByte(chunkSize).toInt())
        val partsMap = mutableMapOf<Int, String>()
        FileInputStream(inputFile).use { inputStream ->
            var bytesRead: Int
            var index = 0
            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                val chunkFile = File(
                    itemCacheDir,
                    "${inputFile.name}.part${index + 1}"
                )
                FileOutputStream(chunkFile).use { outputStream ->
                    outputStream.write(buffer, 0, bytesRead)
                }
                partsMap[index] = chunkFile.absolutePath
                index++
            }
        }
        if (partsMap.isEmpty()) {
            throw Exception("Empty or corrupted file")
        }
        val newItemInfo = ItemFileInfo(
            itemId = itemId,
            folderName = itemCacheDir.name,
            parts = partsMap,
            originalFilePath = inputFile.absolutePath,
            chunkSizeInMb = chunkSize
        )
        jsonFile.writeText(Gson().toJson(newItemInfo))

        return newItemInfo
    }

    private fun itemFileInfo(
        itemCacheDir: File,
        jsonFile: File,
        inputFile: File,
        chunkSize: Int
    ): ItemFileInfo? {
        val result: ItemFileInfo? =
            if (!itemCacheDir.exists()) {
                itemCacheDir.mkdirs()
                null
            } else {
                getOldInfo(
                    jsonFile = jsonFile,
                    originalFile = inputFile,
                    chunkSize = chunkSize,
                    itemCacheDir = itemCacheDir
                )
            }
        if (result == null || !itemCacheDir.exists()) {
            itemCacheDir.mkdirs()
        }
        return result
    }

    private fun chunkSizeInByte(chunkSizeInMb: Int): Long {
        return (chunkSizeInMb * 1024 * 1024).toLong()
    }

    private fun getOldInfo(jsonFile: File, originalFile: File, chunkSize: Int, itemCacheDir: File):
            ItemFileInfo? {

        if (jsonFile.exists()) {
            val itemInfo = try {
                val gson = Gson()
                gson.fromJson(jsonFile.readText(), ItemFileInfo::class.java)
            } catch (_: Exception) {
                itemCacheDir.deleteRecursively()
                return null
            }

            if (itemInfo == null ||
                originalFile.absolutePath != itemInfo.originalFilePath ||
                itemInfo.chunkSizeInMb != chunkSize
            ) {
                itemCacheDir.deleteRecursively()
                return null
            }
            val allExist = itemInfo.parts.values.all { path ->
                val partFile = File(path)
                partFile.exists() && partFile.length() == chunkSizeInByte(chunkSize)
            }
            if (allExist) {
                return itemInfo
            } else {
                itemCacheDir.deleteRecursively()
                return null
            }
        } else {
            return null
        }
    }



    fun createDummyFile(context: Context, fileName: String = "dummyFile.bin"): File {
        val file = File(context.cacheDir, fileName)
        val sizeInBytes = 50L * 1024 * 1024

        try {
            FileOutputStream(file).use { fos ->
                val buffer = ByteArray(1024 * 1024) // 1 ميجابايت buffer
                var written = 0L
                while (written < sizeInBytes) {
                    val bytesToWrite = if (sizeInBytes - written >= buffer.size) buffer.size else (sizeInBytes - written).toInt()
                    fos.write(buffer, 0, bytesToWrite)
                    written += bytesToWrite
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

}

