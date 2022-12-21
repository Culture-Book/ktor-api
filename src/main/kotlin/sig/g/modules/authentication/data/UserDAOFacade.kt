package sig.g.modules.authentication.data

import java.util.*

sealed interface UserDAOFacade {

    suspend fun UUID?.exists(): Boolean
    suspend fun User?.exists(): Boolean
    suspend fun getUser(userId: UUID): User?
    suspend fun registerUser(user: User): User?
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUser(userId: UUID): Boolean
}