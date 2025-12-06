package ru.hqr.tinywms.cripto

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.gson.Gson
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManagerImpl : CryptoManager {

    // Encryption transformation algorithm
    private val ENCRYPTION_TRANSFORMATION = "AES/GCM/NoPadding"
    // Android KeyStore provider
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    // Key alias for the secret key
    private val KEY_ALIAS = "MyKeyAlias"

    // KeyStore instance
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)

    init {
        // Load the KeyStore
        keyStore.load(null)
        // If key alias doesn't exist, create a new secret key
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createSecretKey()
        }
    }

    // Initialize encryption cipher
    override fun initEncryptionCipher(keyName: String): Cipher {
        val cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        return cipher
    }

    // Initialize decryption cipher
    override fun initDecryptionCipher(keyName: String, initializationVector: ByteArray): Cipher {
        val cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION)
        val spec = GCMParameterSpec(128, initializationVector)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        return cipher
    }

    // Encrypt plaintext
    override fun encrypt(plaintext: String, cipher: Cipher): EncryptedData {
        val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
        return EncryptedData(encryptedBytes, cipher.iv)
    }

    // Decrypt ciphertext
    override fun decrypt(ciphertext: ByteArray, cipher: Cipher): String {
        val decryptedBytes = cipher.doFinal(ciphertext)
        return String(decryptedBytes, Charset.forName("UTF-8"))
    }

    // Save encrypted data to SharedPreferences
    override fun saveToPrefs(
        encryptedData: EncryptedData,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ) {
        val json = Gson().toJson(encryptedData)
        with(context.getSharedPreferences(filename, mode).edit()) {
            putString(prefKey, json)
            apply()
        }
    }

    // Retrieve encrypted data from SharedPreferences
    override fun getFromPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): EncryptedData? {
        val json = context.getSharedPreferences(filename, mode).getString(prefKey, null)
        return Gson().fromJson(json, EncryptedData::class.java)
    }

    // Create a new secret key
    private fun createSecretKey() {
        val keyGenParams = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setUserAuthenticationRequired(false)
        }.build()

        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParams)
        keyGenerator.generateKey()
    }

    // Retrieve the secret key from KeyStore
    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
}

fun CryptoManager(): CryptoManager = CryptoManagerImpl()