package sig.g.modules.utils

import java.net.URI

fun String?.toUri() =
    try {
        this?.let { URI.create(it) }
    } catch (e: Exception) {
        null
    }
