package com.gvoltr.compose_image_editor.base

fun <T> List<T>.replace(predicate: (T) -> Boolean, value: T) =
    map { if (predicate(it)) value else it }