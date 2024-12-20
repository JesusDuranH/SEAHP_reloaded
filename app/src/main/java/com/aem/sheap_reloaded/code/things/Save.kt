package com.aem.sheap_reloaded.code.things

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.aem.sheap_reloaded.R
import org.bouncycastle.util.encoders.Base64
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.Serializable

class Save: Serializable {
    private val config = Cipher()

    fun saveOnFile(context: Context, folder: String, tittle: String, content: ByteArray, alias: String){
        val cipherContent = config.cipher(content, alias)
        val data64 = Base64.toBase64String(cipherContent)
        if (ContextCompat.checkSelfPermission
                (context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            try {
                val directoryApp = File(context.getExternalFilesDir(null), folder)
                if (!directoryApp.exists()) directoryApp.mkdir()

                val bufferedWriter = BufferedWriter (FileWriter(File(directoryApp, tittle)))

                bufferedWriter.write(data64)
                bufferedWriter.close()
                Log.d("seahp_Save", "saveOnFile: $tittle")
            } catch (e: Exception) {
                Log.d("seahp_Save", "saveOnFile: ${e.printStackTrace()}")
                config.showMe(context, context.getString(R.string.error_save_write))
                e.message?.let { config.showMe(context, it) }
            }
        } else {
            //config.showMe(context, context.getString(R.string.error_write))
            context.openFileOutput(tittle, Context.MODE_PRIVATE)?.use {
                //
                it.write(data64.toByteArray())
                Log.d("seahp_Save", "saveOnFile: $tittle")
            }
        }
    }

    fun readOnFile(context: Context, folder: String, tittle: String, alias: String): ByteArray?{
        return if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val directoryApp = File(context.getExternalFilesDir(null), folder)
                if (!directoryApp.exists()) directoryApp.mkdir()
                val file = File(directoryApp, tittle)
                val cipher64 = file.readText()
                val cipher = Base64.decode(cipher64)
                Log.d("seahp_Save", "readOnFile: $tittle")
                config.deCipher(cipher, alias)
            } catch (e: Exception){
                config.showMe(context, context.getString(R.string.error_read) +
                        "${e.message}")
                Log.d("seahp_Save", "readOnFile: ${e.printStackTrace()}")
                null
            }
        }
        else{
            var red: ByteArray? = null
            //config.showMe(context, context.getString(R.string.error_read))
            context.openFileInput(tittle)?.use {
                val reader = BufferedReader(InputStreamReader(it))
                val cipher64 = reader.readLine()
                val cipher = Base64.decode(cipher64)
                Log.d("seahp_Save", "readOnFile: $tittle")
                red = config.deCipher(cipher, alias)
            }
            red
        }
    }

    fun saveTempFile(context: Context, tittle: String, content: ByteArray){
        //
        //val file = File.createTempFile(tittle, null, context.cacheDir)
        val file = File(context.cacheDir, tittle)
        Log.d("Save", "Save Temporal File: $file, $content")
        file.writeBytes(content)
    }

    fun readTempFile(context: Context, tittle: String): ByteArray?{
        //
        val file = File(context.cacheDir, tittle)
        Log.d("Save", "Read Temporal File: $file")
        return if (file.exists()){
            //
            Log.d("Save", "File Exist: ${file.exists()}")
            file.readBytes()
        } else null
    }

    fun deleteTempFile(context: Context, tittle: String){
        //
        val file = File(context.cacheDir, tittle)
        Log.d("Save", "File: $file")
        if (file.exists()){
            //
            try {
                file.delete()
                Log.d("Save", "Delete: ${file.exists()}")
            } catch (ex: Exception) {
                Log.d("Save", "Error Delete: ${ex.printStackTrace()}")
            }
        }
    }
}