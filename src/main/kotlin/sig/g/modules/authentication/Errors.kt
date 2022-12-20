package sig.g.modules.authentication

sealed interface AuthError {
    object InvalidEmail : AuthError
    object DatabaseError : AuthError
}