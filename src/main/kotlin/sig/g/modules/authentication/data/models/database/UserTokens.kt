package sig.g.modules.authentication.data.models.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UserTokens : Table() {
    val userTokenId = uuid("userTokenId").autoGenerate()
    val userId = text("userId").references(Users.userId)
    val accessToken = uuid("accessToken")

    override val primaryKey: PrimaryKey = PrimaryKey(userTokenId)
}


