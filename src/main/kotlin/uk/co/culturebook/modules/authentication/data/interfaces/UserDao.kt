package uk.co.culturebook.modules.authentication.data.interfaces

import uk.co.culturebook.modules.authentication.data.models.User

interface UserDao {
    suspend fun String.exists(): Boolean
    suspend fun getUser(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun registerUser(user: User): User?
    suspend fun updateTos(userId: String): Boolean
    suspend fun updatePrivacy(userId: String): Boolean
    suspend fun updatePassword(userId: String, password: String): Boolean
    suspend fun deleteUser(userId: String): Boolean
}