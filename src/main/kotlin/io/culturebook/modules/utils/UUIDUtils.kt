package io.culturebook.modules.utils

import java.util.*

fun String?.toUUID(): UUID = UUID.fromString(this)