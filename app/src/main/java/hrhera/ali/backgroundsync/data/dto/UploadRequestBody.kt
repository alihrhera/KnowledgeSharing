package hrhera.ali.backgroundsync.data.dto


data class UploadRequestBody (
    val itemId: String,
    val part: String,
    val partIndex: Int
){
    fun toRequestPart(){
        // add retro fit impl to return multipart
    }
}