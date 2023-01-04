package uk.co.culturebook.modules.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*
import uk.co.culturebook.modules.authentication.data.AuthConfig.issuer
import uk.co.culturebook.modules.authentication.data.AuthConfig.jwtExpiry
import uk.co.culturebook.modules.authentication.data.AuthConfig.privateKey
import uk.co.culturebook.modules.authentication.data.interfaces.JwtClaim
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE

// OF THE PKCS8 FORMAT, WILL NOT WORK OTHERWISE
// To generate a private rsa key do : openssl genrsa -out <private_key> 2048
// To generate a public rsa key do : openssl rsa -in <private_key> -pubout -outform PEM -out <public_key>
// To convert the private key to PKCS8 do : openssl pkcs8 -topk8 -inform PEM -in <private_key> -out <pkcs8_key> -nocrypt

private val ApplicationConfig.privateOAuthKey: PrivateKey get() = generateJavaPrivateKey(privateKey)

val ApplicationConfig.jwtVerifier: JWTVerifier
    get() = JWT
        .require(Algorithm.HMAC256(privateKey))
        .withIssuer(issuer)
        .build()

private fun generateJavaPrivateKey(privateKey: String): PrivateKey {
    val keyFactory = KeyFactory.getInstance("RSA")
    val privateKeyDecoded = Base64.getDecoder().decode(privateKey)
    val privateEncodedKeySpec = PKCS8EncodedKeySpec(privateKeyDecoded)
    return keyFactory.generatePrivate(privateEncodedKeySpec)
}

private fun String.decrypt(key: PrivateKey): String? {
    return try {
        val decryptCipher = Cipher.getInstance("RSA")
            .apply {
                init(DECRYPT_MODE, key)
            }
        val stringBytes = Base64.getDecoder().decode(this)
        val secretBytes = decryptCipher.doFinal(stringBytes)

        String(secretBytes)
    } catch (e: Exception) {
        print(e.message)
        // TODO - proper logging, firebase perhaps?
        null
    }
}

fun String.decodeOAuth(applicationConfig: ApplicationConfig) = decrypt(applicationConfig.privateOAuthKey)

fun generateAccessJwt(config: ApplicationConfig, userId: String, accessToken: UUID): String? {
    return JWT.create()
        .withIssuer(config.issuer)
        .withClaim(JwtClaim.UserId.claim, userId)
        .withClaim(JwtClaim.AccessToken.claim, accessToken.toString())
        .withExpiresAt(
            Date(System.currentTimeMillis() + config.jwtExpiry.toLong())
        )
        .sign(Algorithm.HMAC256(config.privateKey))
}
