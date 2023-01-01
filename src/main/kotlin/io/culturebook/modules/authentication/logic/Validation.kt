package io.culturebook.modules.authentication.logic


fun String?.isProperEmail() = Regex("^([a-zA-Z0-9_\\-]+)@([a-zA-Z0-9_\\-]+)\\.([a-zA-Z]{2,5})\$").matches(this ?: "")