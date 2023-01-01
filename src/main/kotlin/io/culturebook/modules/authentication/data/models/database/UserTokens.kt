package io.culturebook.modules.authentication.data.models.database

import org.jetbrains.exposed.sql.Table

object UserTokens : Table() {
    val userTokenId = uuid("userTokenId").autoGenerate()
    val userId = text("userId").references(Users.userId)
    val accessToken = uuid("accessToken")
    val refreshToken = uuid("refreshToken")

    override val primaryKey: PrimaryKey = PrimaryKey(userTokenId)
}


