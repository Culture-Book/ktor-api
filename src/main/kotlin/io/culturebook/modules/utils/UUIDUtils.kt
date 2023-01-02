package io.culturebook.modules.utils

import java.util.*

fun String?.toUUID(): UUID = UUID.fromString(this)

fun String?.generateUUID(): UUID = if (!isNullOrEmpty()) UUID.nameUUIDFromBytes(toByteArray()) else UUID.randomUUID()