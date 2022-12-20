package sig.g.modules.utils

import java.time.LocalDateTime

fun LocalDateTime?.toTimeStamp() = this?.toString()
fun String?.toLocalDateTime(): LocalDateTime? = try {
    LocalDateTime.parse(this)
} catch (e: Exception) {
    null
}