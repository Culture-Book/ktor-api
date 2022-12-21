package sig.g.modules.utils

import java.util.UUID

fun String?.toUUID() =
    try {
        UUID.fromString(this)
    } catch (e: Exception) {
        null
    }