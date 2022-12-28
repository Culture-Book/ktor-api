package sig.g.modules.authentication.data.models.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table() {
    val userId = text("userId")
    val profileUri = text("profileUri").nullable()
    val displayName = text("displayName").nullable()
    val password = text("password")
    val email = text("email")
    val tosAccept = datetime("tosAccept").nullable()
    val privacyAccept = datetime("privacyAccept").nullable()
    val verificationStatus = integer("verificationStatus")
    val registrationStatus = integer("registrationStatus")
    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}
