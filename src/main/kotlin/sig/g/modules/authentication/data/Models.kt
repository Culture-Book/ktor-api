package sig.g.modules.authentication.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import sig.g.data_access.serializers.LocalDateTimeSerializer
import sig.g.data_access.serializers.URISerializer
import sig.g.data_access.serializers.UUIDSerializer
import java.net.URI
import java.time.LocalDateTime
import java.util.*

data class UserSession(val accessToken: String, val refreshToken: String)

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID = UUID.randomUUID(),
    @Serializable(with = URISerializer::class)
    val profileUri: URI? = null,
    val displayName: String? = null,
    val password: String,
    val email: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val tosAccept: LocalDateTime? = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val privacyAccept: LocalDateTime? = LocalDateTime.now(),
    val verificationStatus: Int = VerificationStatus.NotVerified.ordinal,
    val registrationStatus: Int = RegistrationStatus.Pending.ordinal
)

object Users : Table() {
    val userId = uuid("userId").autoGenerate()
    val profileUri = varchar("profileUri", 255).nullable()
    val displayName = varchar("displayName", 100).nullable()
    val password = varchar("password", 2048)
    val email = varchar("email", 2048)
    val tosAccept = varchar("tosAccept", 25).nullable()
    val privacyAccept = varchar("privacyAccept", 25).nullable()
    val verificationStatus = integer("verificationStatus")
    val registrationStatus = integer("registrationStatus")
    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}

enum class VerificationStatus {
    NotVerified, Verified, Pending
}

enum class RegistrationStatus {
    Pending, Registered
}