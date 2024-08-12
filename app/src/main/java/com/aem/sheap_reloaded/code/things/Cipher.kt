package com.aem.sheap_reloaded.code.things

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyStore
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class Cipher() {

    private fun getKey(alias: String): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        return if (!keyStore.containsAlias(alias)) {
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            val builder = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).run {
                setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                setRandomizedEncryptionRequired(false)
            }
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        } else keyStore.getKey(alias, null) as SecretKey
    }

    fun cipher(data: ByteArray, alias: String): ByteArray {
        Security.addProvider(BouncyCastleProvider())
        val key = getKey(alias)
        val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/" +
                "${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        return iv + cipher.doFinal(data)
    }

    fun deCipher(data: ByteArray, alias: String): ByteArray {
        val key = getKey(alias)
        val getIV = data.copyOfRange(0, 16)
        val cleanData = data.copyOfRange(16, data.size)
        val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/" +
                "${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}")
        val ivSpec = IvParameterSpec(getIV)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        return cipher.doFinal(cleanData)
    }

    fun showMe(context: Context, string: String){
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }

}