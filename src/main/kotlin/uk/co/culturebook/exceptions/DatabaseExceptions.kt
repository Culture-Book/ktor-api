package uk.co.culturebook.exceptions

data class DatabaseNotInitialised(override val message: String?) : Exception()
