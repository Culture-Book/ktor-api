package sig.g.modules.utils

import java.util.*

fun String?.toUUID() = UUID.fromString(this)