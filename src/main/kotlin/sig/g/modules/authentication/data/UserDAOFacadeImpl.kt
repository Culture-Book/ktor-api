package sig.g.modules.authentication.data

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import sig.g.data_access.dbQuery
import sig.g.modules.utils.toLocalDateTime
import sig.g.modules.utils.toTimeStamp
import sig.g.modules.utils.toUri
import java.util.*

object UserDAOFacadeImpl : UserDAOFacade {

    private fun resultRowToUser(row: ResultRow) = User(
        userId = row[Users.userId],
        profileUri = row[Users.profileUri].toUri(),
        displayName = row[Users.displayName],
        password = row[Users.password],
        email = row[Users.email],
        tosAccept = row[Users.tosAccept].toLocalDateTime(),
        privacyAccept = row[Users.privacyAccept].toLocalDateTime(),
        verificationStatus = row[Users.verificationStatus],
        registrationStatus = row[Users.registrationStatus],
    )

    override suspend fun User?.exists(): Boolean = dbQuery {
        if (this == null) false else Users.select(Users.email eq email).singleOrNull() != null
    }

    override suspend fun getUser(userId: UUID): User? =
        Users.select(Users.userId eq userId)
            .map(::resultRowToUser)
            .singleOrNull()

    override suspend fun registerUser(user: User): User? = dbQuery {
        val insertStatement = Users.insert {
            it[userId] = (user.userId)
            it[profileUri] = user.profileUri.toString()
            it[displayName] = user.displayName ?: ""
            it[password] = user.password
            it[email] = user.email
            it[tosAccept] = user.tosAccept.toTimeStamp()
            it[privacyAccept] = user.privacyAccept.toTimeStamp()
            it[verificationStatus] = user.verificationStatus
            it[registrationStatus] = user.registrationStatus
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun updateUser(user: User): Boolean = dbQuery {
        Users.update {
            it[userId] = user.userId
            it[profileUri] = user.profileUri.toString()
            it[displayName] = user.displayName ?: ""
            it[password] = user.password
            it[email] = user.email
            it[tosAccept] = user.tosAccept.toTimeStamp()
            it[privacyAccept] = user.privacyAccept.toTimeStamp()
            it[verificationStatus] = user.verificationStatus
            it[registrationStatus] = user.registrationStatus
        } > 0
    }

    override suspend fun deleteUser(userId: UUID): Boolean = dbQuery {
        Users.deleteWhere { Users.userId eq userId } > 0
    }
}