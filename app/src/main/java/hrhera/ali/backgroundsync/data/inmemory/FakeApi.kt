package hrhera.ali.backgroundsync.data.inmemory

import hrhera.ali.backgroundsync.data.dto.UploadRequestBody
import hrhera.ali.backgroundsync.data.dto.UploadResponse
import hrhera.ali.backgroundsync.data.network.UploadService
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeApi @Inject constructor() : UploadService {
    private val list = mutableListOf<Pair<String, Int>>()
    override suspend fun uploadPart(request: UploadRequestBody): UploadResponse {
        delay(1000)
        val itemWithIndex = getItemWithIndex(request.itemId)
        if (itemWithIndex.first > -1) {
            list[itemWithIndex.first] = Pair(request.itemId, request.partIndex)
        } else {
            list.add(Pair(request.itemId, request.partIndex))
        }
        return UploadResponse(request.partIndex)
    }

    private fun getItemWithIndex(itemId: String): Pair<Int, Pair<String, Int>> {
        val i = list.indexOfFirst { it.first == itemId }
        return Pair(i, if (i > -1) list[i] else Pair(itemId, -1))
    }

    override suspend fun getLastUploadedItem(itemId: String): UploadResponse {
        delay(1000)
        val itemWithIndex = getItemWithIndex(itemId)
        return UploadResponse(itemWithIndex.second.second)

    }
}