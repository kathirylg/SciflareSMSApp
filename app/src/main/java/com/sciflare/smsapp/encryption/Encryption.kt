package com.sciflare.smsapp.encryption

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Encryption {
    companion object {
        var IVP_KEY = ByteArray(16)

        @Throws(java.lang.Exception::class)
        fun encrypt(plaintext: ByteArray, key: ByteArray, IV: ByteArray): String {
            val cipher = Cipher.getInstance("AES")
            val keySpec = SecretKeySpec(key, "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val bytArray = cipher.doFinal(plaintext)
            return Base64.encodeToString(bytArray, Base64.NO_WRAP)
        }

        fun decrypt(byteArray: ByteArray, key: ByteArray, IV: ByteArray): String? {
            try {
                val cipherText = Base64.decode(byteArray,Base64.NO_WRAP)
                val cipher = Cipher.getInstance("AES")
                val keySpec = SecretKeySpec(key, "AES")
                val ivSpec = IvParameterSpec(IV)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
                val decryptedText = cipher.doFinal(cipherText)
                return String(decryptedText)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}