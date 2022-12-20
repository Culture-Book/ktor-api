package sig.g.modules.authentication

import sig.g.config.AppConfig
import sig.g.config.getProperty
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
private val publicKey: PublicKey
    get() {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = AppConfig.JWTConfig.PublicKey.getProperty()
        val publicKeyDecoded = Base64.getDecoder().decode(publicKey)
        val publicEncodedKeySpec = X509EncodedKeySpec(publicKeyDecoded)
        return keyFactory.generatePublic(publicEncodedKeySpec)
    }

// OF THE PKCS8 FORMAT, WILL NOT WORK OTHERWISE
// To generate a private rsa key do : openssl genrsa -out <private_key> 2048
// To generate a public rsa key do : openssl rsa -in <private_key> -pubout -outform PEM -out <public_key>
// To convert the private key to PKCS8 do : openssl pkcs8 -topk8 -inform PEM -in <private_key> -out <pkcs8_key> -nocrypt
private val privateKey: PrivateKey
    get() {
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = AppConfig.JWTConfig.PrivateKey.getProperty()
        val privateKeyDecoded = Base64.getDecoder().decode(privateKey)
        val privateEncodedKeySpec = PKCS8EncodedKeySpec(privateKeyDecoded)
        return keyFactory.generatePrivate(privateEncodedKeySpec)
    }

fun String.encrypt(): String? {
    return try {
        val encryptCipher = Cipher.getInstance("RSA")
            .apply {
                init(ENCRYPT_MODE, publicKey)
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

fun String.decrypt(): String? {
    return try {
        val decryptCipher = Cipher.getInstance("RSA")
            .apply {
                init(DECRYPT_MODE, privateKey)
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