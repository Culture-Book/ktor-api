package uk.co.culturebook.utils

import java.util.*

fun String?.toUUID(): UUID = UUID.fromString(this)

fun String?.toUUIDOrRandom(): UUID =
    if (!isNullOrEmpty()) UUID.nameUUIDFromBytes(toByteArray()) else UUID.randomUUID()