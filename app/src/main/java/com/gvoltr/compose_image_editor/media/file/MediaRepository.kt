package com.gvoltr.compose_image_editor.media.file

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.gvoltr.compose_image_editor.media.bitmap.BitmapDrawing
import com.gvoltr.compose_image_editor.media.bitmap.ImageEditor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MediaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileInfoProvider: FileInfoProvider,
    private val imageEditor: ImageEditor
) {

    private val logTag = "FileStorage"

    suspend fun createTempImageFile() = createCacheFile(
        subdirectory = "img",
        filename = "img${System.currentTimeMillis()}.jpg"
    ).toUri()

    suspend fun createTempVideoFile() = createCacheFile(
        subdirectory = "video",
        filename = "vid${System.currentTimeMillis()}.mp4"
    ).toUri()

    private suspend fun createCacheFile(subdirectory: String, filename: String): File {
        val createdFile = withContext(Dispatchers.IO) {
            val file = File("${context.cacheDir.absolutePath}/$subdirectory/$filename")
            try {
                if (!file.parentFile.exists()) file.parentFile.mkdirs()
                if (!file.exists()) file.createNewFile()
            } catch (error: SecurityException) {
                Log.e(logTag, error.stackTraceToString())
            } catch (error: IOException) {
                Log.e(logTag, error.stackTraceToString())
            }
            file
        }
        return createdFile
    }

    suspend fun delete(uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                uri.toFile().delete()
            } catch (error: SecurityException) {
                Log.e(logTag, error.stackTraceToString())
            } catch (error: IllegalArgumentException) {
                Log.e(logTag, error.stackTraceToString())
            }
        }
    }

    suspend fun drawOnImage(uri: Uri, drawings: List<BitmapDrawing>): Uri {
        val outputImage = createTempImageFile()
        imageEditor.drawOnImage(reference = uri, outputImage = outputImage, drawings = drawings)
        if (fileInfoProvider.isFromAppCache(uri)) {
            delete(uri)
        }
        return outputImage
    }
}
