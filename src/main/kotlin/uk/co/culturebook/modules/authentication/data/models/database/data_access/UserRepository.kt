package uk.co.culturebook.modules.authentication.data.models.database.data_access

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import uk.co.culturebook.data_access.dbQuery
import uk.co.culturebook.modules.authentication.data.models.User
import uk.co.culturebook.modules.authentication.data.models.database.Users
import uk.co.culturebook.modules.utils.toUri
import java.time.LocalDateTime

object UserRepository : UserDao {

    private fun resultRowToUser(row: ResultRow) = User(
        userId = row[Users.userId],
        profileUri = row[Users.profileUri].toUri(),
        displayName = row[Users.displayName],
        password = row[Users.password],
        email = row[Users.email],
        tosAccept = row[Users.tosAccept],
        privacyAccept = row[Users.privacyAccept],
        verificationStatus = row[Users.verificationStatus],
        registrationStatus = row[Users.registrationStatus],
    )

    override suspend fun String.exists(): Boolean = dbQuery {
        Users.select(Users.email eq this).singleOrNull() != null
    }

    override suspend fun getUser(userId: String): User? = dbQuery {
        Users.select(Users.userId eq userId)
            .map(UserRepository::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select(Users.email eq email)
            .map(UserRepository::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun registerUser(user: User): User? = dbQuery {
        val insertStatement = Users.insert {
            it[userId] = user.userId
            it[profileUri] = user.profileUri.toString()
            it[displayName] = user.displayName
            it[password] = user.password
            it[email] = user.email
            it[tosAccept] = LocalDateTime.now()
            it[privacyAccept] = LocalDateTime.now()
            it[verificationStatus] = user.verificationStatus
            it[registrationStatus] = user.registrationStatus
        }
        insertStatement.resultedValues?.singleOrNull()?.let(UserRepository::resultRowToUser)
    }

    override suspend fun updateTos(userId: String): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[tosAccept] = LocalDateTime.now()
        } > 0
    }

    override suspend fun updatePrivacy(userId: String): Boolean = dbQuery {
        Users.update({ Users.userId eq userId }) {
            it[privacyAccept] = LocalDateTime.now()
        } > 0
    }

    override suspend fun updatePassword(email: String, password: String): Boolean = dbQuery {
        Users.update({ Users.email eq email }) {
            it[Users.password] = password
        } > 0
    }

    override suspend fun deleteUser(userId: String): Boolean = dbQuery {
        Users.deleteWhere { Users.userId eq userId } > 0
    }
}