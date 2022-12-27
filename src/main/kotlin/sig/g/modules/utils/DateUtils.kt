package sig.g.modules.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun addSeconds(amount: Int): LocalDateTime? = LocalDateTime.from(
    Instant.now().atOffset(ZoneOffset.ofTotalSeconds(amount))
)