package hrhera.ali.backgroundsync.util.compressers

sealed class CompressStatus {
    data class Success(val outPutFilePath: String,val newSize:String) : CompressStatus()
    data object Failed : CompressStatus()
}