package uk.co.culturebook.utils

import java.util.*

fun String?.toUUID(): UUID = UUID.fromString(this)

fun String?.generateUUID(): UUID =
    if (!isNullOrEmpty()) UUID.nameUUIDFromBytes(toByteArray()) else throw IllegalArgumentException()