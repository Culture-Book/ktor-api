package uk.co.culturebook.modules.authentication.data.database.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.modules.authentication.data.database.tables.PasswordResets
import uk.co.culturebook.modules.authentication.data.database.tables.PasswordResets.expiresAt
import uk.co.culturebook.modules.authentication.data.database.tables.PasswordResets.passwordResetToken
import uk.co.culturebook.modules.authentication.data.database.tables.PasswordResets.userId
import uk.co.culturebook.modules.authentication.data.interfaces.PasswordResetDao
import uk.co.culturebook.modules.authentication.data.models.PasswordResetRequest
import uk.co.culturebook.modules.database.dbQuery
import java.time.LocalDateTime
import java.util.*

object PasswordResetRepository : PasswordResetDao {
    private fun rowToResult(resultRow: ResultRow) = PasswordResetRequest(
        userId = resultRow[userId],
        passwordResetToken = resultRow[passwordResetToken],
        expiresAt = resultRow[expiresAt]
    )

    override suspend fun insertPasswordReset(passwordResetRequest: PasswordResetRequest): PasswordResetRequest? =
        dbQuery {
            val statement = PasswordResets.insert {
                it[userId] = passwordResetRequest.userId
                it[passwordResetToken] = passwordResetRequest.passwordResetToken
                it[expiresAt] = passwordResetRequest.expiresAt ?: LocalDateTime.now()
            }

            statement.resultedValues?.singleOrNull()?.let(PasswordResetRepository::rowToResult)
        }

    override suspend fun deletePasswordReset(userId: String): Boolean = dbQuery {
        PasswordResets.deleteWhere { (PasswordResets.userId eq userId) } > 0
    }

    override suspend fun getPasswordReset(userId: String, token: UUID): PasswordResetRequest? = dbQuery {
        PasswordResets.select { (PasswordResets.userId eq userId) and (passwordResetToken eq token) }
            .singleOrNull()?.let(PasswordResetRepository::rowToResult)
    }

}