package com.gvoltr.compose_image_editor.media

interface MediaFile {
    val thumbnail: String
    val path: String
    val fileType: FileType
    val uriType: UriType
}