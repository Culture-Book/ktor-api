package sig.g.modules.authentication.data.data_access

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import sig.g.data_access.dbQuery
import sig.g.modules.authentication.data.models.UserToken
import sig.g.modules.authentication.data.models.database.UserTokens
import java.time.LocalDateTime

object UserTokenRepository : UserTokenDao {

    private fun rowToUserToken(row: ResultRow): UserToken = UserToken(
        tokenId = row[UserTokens.userTokenId],
        userId = row[UserTokens.userId],
        accessToken = row[UserTokens.accessToken],
        refreshToken = row[UserTokens.refreshToken],
        expiresAt = row[UserTokens.expiresAt],
    )

    override suspend fun getUserToken(userToken: UserToken): UserToken? = dbQuery {
        UserTokens.select(
            (UserTokens.accessToken eq userToken.accessToken) and
                    (UserTokens.refreshToken eq userToken.refreshToken) and
                    (UserTokens.userId eq userToken.userId)
        ).singleOrNull()?.let(UserTokenRepository::rowToUserToken)
    }

    override suspend fun userTokenExists(userToken: UserToken): Boolean = dbQuery {
        UserTokens.select(
            (UserTokens.accessToken eq userToken.accessToken) and
                    (UserTokens.refreshToken eq userToken.refreshToken) and
                    (UserTokens.userId eq userToken.userId)
        ).singleOrNull() != null
    }

    override suspend fun insertToken(userToken: UserToken): UserToken? = dbQuery {
        val statement = UserTokens.insert {
            it[userId] = userToken.userId
            it[accessToken] = userToken.accessToken
            it[refreshToken] = userToken.refreshToken
            it[expiresAt] = userToken.expiresAt ?: LocalDateTime.now()
        }

        statement.resultedValues?.singleOrNull()?.let(UserTokenRepository::rowToUserToken)
    }

    override suspend fun deleteToken(userId: String): Boolean = dbQuery {
        UserTokens.deleteWhere { (this.userId eq userId) } > 0
    }

    override suspend fun updateToken(userToken: UserToken): Boolean = dbQuery {
        UserTokens.update({ UserTokens.userId eq userToken.userId }) {
            it[accessToken] = accessToken
            it[refreshToken] = refreshToken
            it[expiresAt] = expiresAt
        } > 0
    }

    override suspend fun getUserTokens(userId: String): List<UserToken> = dbQuery {
        UserTokens.select(UserTokens.userId eq userId).map(UserTokenRepository::rowToUserToken)
    }
}