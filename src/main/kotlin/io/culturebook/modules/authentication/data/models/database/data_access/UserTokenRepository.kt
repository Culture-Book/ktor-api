package io.culturebook.modules.authentication.data.models.database.data_access

import io.culturebook.data_access.dbQuery
import io.culturebook.modules.authentication.data.models.UserToken
import io.culturebook.modules.authentication.data.models.database.UserTokens
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

object UserTokenRepository : UserTokenDao {

    private fun rowToUserToken(row: ResultRow): UserToken = UserToken(
        tokenId = row[UserTokens.userTokenId],
        userId = row[UserTokens.userId],
        accessToken = row[UserTokens.accessToken],
        refreshToken = row[UserTokens.refreshToken]
    )

    override suspend fun getUserToken(userToken: UserToken): UserToken? = dbQuery {
        UserTokens.select(
            (UserTokens.accessToken eq userToken.accessToken) and
                    (UserTokens.userId eq userToken.userId)
        ).singleOrNull()?.let(UserTokenRepository::rowToUserToken)
    }

    override suspend fun userTokenExists(userToken: UserToken): Boolean = dbQuery {
        !UserTokens.select(
            ((UserTokens.accessToken eq userToken.accessToken) or (UserTokens.refreshToken eq userToken.refreshToken!!)) and
                    (UserTokens.userId eq userToken.userId)
        ).empty()
    }

    override suspend fun insertToken(userToken: UserToken): UserToken? = dbQuery {
        val statement = UserTokens.insert {
            it[userId] = userToken.userId
            it[accessToken] = userToken.accessToken
            it[refreshToken] = userToken.refreshToken ?: UUID.randomUUID()
        }

        statement.resultedValues?.singleOrNull()?.let(UserTokenRepository::rowToUserToken)
    }

    override suspend fun deleteToken(userId: String): Boolean = dbQuery {
        UserTokens.deleteWhere { (this.userId eq userId) } > 0
    }

    override suspend fun updateToken(userToken: UserToken): Boolean = dbQuery {
        UserTokens.update({ UserTokens.userId eq userToken.userId }) {
            it[accessToken] = accessToken
        } > 0
    }

    override suspend fun getUserTokens(userId: String): List<UserToken> = dbQuery {
        UserTokens.select(UserTokens.userId eq userId).map(UserTokenRepository::rowToUserToken)
    }
}