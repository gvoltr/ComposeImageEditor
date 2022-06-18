package com.gvoltr.compose_image_editor.media.file

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.gvoltr.compose_image_editor.media.FileType
import com.gvoltr.compose_image_editor.media.UriType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class FileInfoProvider @Inject constructor(@ApplicationContext private val context: Context) {

    fun getFileType(uri: Uri): FileType {
        val mime = context.contentResolver.getType(uri)
        return when {
            mime?.contains("image") ?: false -> FileType.Image
            mime?.contains("video") ?: false -> FileType.Video
            else -> FileType.Unknown
        }
    }

    fun getUriType(uri: Uri): UriType {
        return when {
            uri.toString().startsWith("content") -> UriType.Content
            uri.toString().startsWith("file") -> UriType.File
            else -> UriType.Unknown
        }
    }

    /**
     * Produce total video time in 'mm:ss' format
     */
    fun getVideoLength(uri: Uri): String {
        val durationMillis = getVideoLengthMillis(uri)
        val totalSeconds = durationMillis / 1000
        val minutes = totalSeconds / 60 % 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    private fun getVideoLengthMillis(videoUri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, videoUri)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillis = time?.toLong()
        retriever.release()
        return timeInMillis ?: 0
    }

    fun isFromAppCache(uri: Uri) =
        uri.path?.contains(context.cacheDir.absolutePath, true) ?: false
}
