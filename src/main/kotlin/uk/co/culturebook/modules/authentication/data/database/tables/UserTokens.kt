package uk.co.culturebook.modules.authentication.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserTokens : Table() {
    val userTokenId = uuid("userTokenId").autoGenerate()
    val userId =
        text("userId").references(Users.userId, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val accessToken = uuid("accessToken")
    val refreshToken = uuid("refreshToken")

    override val primaryKey: PrimaryKey = PrimaryKey(userTokenId)
}


