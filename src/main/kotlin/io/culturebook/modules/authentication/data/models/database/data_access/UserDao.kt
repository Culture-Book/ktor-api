package io.culturebook.modules.authentication.data.models.database.data_access

import io.culturebook.modules.authentication.data.models.User

sealed interface UserDao {

    suspend fun String.exists(): Boolean
    suspend fun getUser(userId: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun registerUser(user: User): User?
    suspend fun updateTos(userId: String): Boolean
    suspend fun updatePrivacy(userId: String): Boolean
    suspend fun deleteUser(userId: String): Boolean
}