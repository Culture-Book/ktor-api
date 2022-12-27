package sig.g.modules.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import sig.g.config.AppConfig
import sig.g.config.getProperty
import sig.g.modules.authentication.data.models.JwtClaim
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE

// OF THE PKCS8 FORMAT, WILL NOT WORK OTHERWISE
// To generate a private rsa key do : openssl genrsa -out <private_key> 2048
// To generate a public rsa key do : openssl rsa -in <private_key> -pubout -outform PEM -out <public_key>
// To convert the private key to PKCS8 do : openssl pkcs8 -topk8 -inform PEM -in <private_key> -out <pkcs8_key> -nocrypt

private val publicJwtKey: PublicKey = generateJavaPublicKey(AppConfig.JWTConfig.PublicKey.getProperty())
private val privateJwtKey: PrivateKey = generateJavaPrivateKey(AppConfig.JWTConfig.PrivateKey.getProperty())

private val publicOAuthKey: PublicKey = generateJavaPublicKey(AppConfig.OAuthConfig.PublicKey.getProperty())
private val privateOAuthKey: PrivateKey = generateJavaPrivateKey(AppConfig.OAuthConfig.PrivateKey.getProperty())

val jwtVerifier: JWTVerifier = JWT
    .require(Algorithm.HMAC256(AppConfig.JWTConfig.PrivateKey.getProperty()))
    .withIssuer(AppConfig.JWTConfig.Issuer.getProperty())
    .build()

private fun generateJavaPublicKey(publicKey: String): PublicKey {
    val keyFactory = KeyFactory.getInstance("RSA")
    val publicKeyDecoded = Base64.getDecoder().decode(publicKey)
    val publicEncodedKeySpec = X509EncodedKeySpec(publicKeyDecoded)
    return keyFactory.generatePublic(publicEncodedKeySpec)
}

private fun generateJavaPrivateKey(privateKey: String): PrivateKey {
    val keyFactory = KeyFactory.getInstance("RSA")
    val privateKeyDecoded = Base64.getDecoder().decode(privateKey)
    val privateEncodedKeySpec = PKCS8EncodedKeySpec(privateKeyDecoded)
    return keyFactory.generatePrivate(privateEncodedKeySpec)
}

private fun String.encrypt(key: PublicKey): String? {
    return try {
        val encryptCipher = Cipher.getInstance("RSA")
            .apply {
                init(ENCRYPT_MODE, key)
            }
        val stringBytes = this.toByteArray()
        val secretBytes = encryptCipher.doFinal(stringBytes)

        Base64.getEncoder().encodeToString(secretBytes)
    } catch (e: Exception) {
        // TODO - proper logging, firebase perhaps?
        print(e.message)
        null
    }
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

//fun String.encodeJwt() = encrypt(publicJwtKey)
fun String.decodeJwt() = decrypt(privateJwtKey)

fun String.encodeOAuth() = encrypt(publicOAuthKey)
fun String.decodeOAuth() = decrypt(privateOAuthKey)

fun generateAccessJwt(userId: String, accessToken: UUID, refreshToken: UUID): String? {
    val issuer = AppConfig.JWTConfig.Issuer.getProperty()
    return JWT.create()
        .withIssuer(issuer)
        .withClaim(JwtClaim.UserId.claim, userId)
        .withClaim(JwtClaim.AccessToken.claim, accessToken.toString())
        .withClaim(JwtClaim.RefreshToken.claim, refreshToken.toString())
        .withExpiresAt(
            Date(System.currentTimeMillis() + AppConfig.JWTConfig.RefreshTokenExpiry.getProperty().toLong())
        )
        .sign(Algorithm.HMAC256(AppConfig.JWTConfig.PrivateKey.getProperty()))
}