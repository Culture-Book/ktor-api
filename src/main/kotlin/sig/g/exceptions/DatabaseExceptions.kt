package sig.g.exceptions

data class DatabaseNotInitialised(override val message: String?) : Exception()
