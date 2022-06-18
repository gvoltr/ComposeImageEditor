package com.gvoltr.compose_image_editor.media

import android.net.Uri

data class LocalFile(
    val uri: Uri,
    override val uriType: UriType,
    override val fileType: FileType,
    val duration: String? = null
) : MediaFile {
    override val thumbnail = uri.toString()
    override val path = uri.toString()
}
