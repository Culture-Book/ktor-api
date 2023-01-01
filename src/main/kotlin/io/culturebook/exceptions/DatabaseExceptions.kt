package io.culturebook.exceptions

data class DatabaseNotInitialised(override val message: String?) : Exception()
