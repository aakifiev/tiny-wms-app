package ru.hqr.tinywms.cripto

import android.content.Context
import javax.crypto.Cipher

interface CryptoManager {

    // Initialize encryption cipher
    fun initEncryptionCipher(keyName: String): Cipher

    // Initialize decryption cipher
    fun initDecryptionCipher(keyName: String, initializationVector: ByteArray): Cipher

    // Encrypt plaintext
    fun encrypt(plaintext: String, cipher: Cipher): EncryptedData

    // Decrypt ciphertext
    fun decrypt(ciphertext: ByteArray, cipher: Cipher): String

    // Save encrypted data to SharedPreferences
    fun saveToPrefs(
        encryptedData: EncryptedData,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    )

    // Retrieve encrypted data from SharedPreferences
    fun getFromPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): EncryptedData?
}