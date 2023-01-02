package uk.co.culturebook.modules.authentication.data.models.database.data_access

import uk.co.culturebook.modules.authentication.data.models.UserToken

sealed interface UserTokenDao {

    suspend fun getUserToken(userToken: UserToken): UserToken?

    suspend fun userTokenExists(userToken: UserToken): Boolean

    suspend fun insertToken(userToken: UserToken): UserToken?

    suspend fun deleteToken(userId: String): Boolean

    suspend fun updateToken(userToken: UserToken): Boolean

    suspend fun getUserTokens(userId: String): List<UserToken>
}