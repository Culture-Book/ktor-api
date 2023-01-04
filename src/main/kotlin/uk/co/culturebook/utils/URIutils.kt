package uk.co.culturebook.utils

import java.net.URI

fun String?.toUri() =
    try {
        this?.let { URI.create(it) }
    } catch (e: Exception) {
        null
    }
