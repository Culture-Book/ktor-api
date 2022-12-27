package sig.g.modules.authentication.data

import sig.g.modules.authentication.data.models.User

sealed interface UserDao {

    suspend fun String.exists(): Boolean
    suspend fun getUser(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun registerUser(user: User): User?
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUser(userId: String): Boolean
}