package com.aem.sheap_reloaded.code.things

import android.content.Context
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

class SEAHP() {
    private val save = Save()

    fun setStatus(isEdit: Boolean, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_project_edit),
            booleanToByteArray(isEdit),
            context.getString(R.string.alias_edit))
    }

    fun getStatus(context: Context): Boolean{
        val isEdit = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_project_edit),
            context.getString(R.string.alias_edit))
        return byteArrayToBoolean(isEdit!!)
    }

    private fun booleanToByteArray(value: Boolean): ByteArray {
        val outputStream = ByteArrayOutputStream()
        DataOutputStream(outputStream).use {
            it.writeBoolean(value)
        }
        return outputStream.toByteArray()
    }

    private fun byteArrayToBoolean(byteArray: ByteArray): Boolean {
        val inputStream = ByteArrayInputStream(byteArray)
        return DataInputStream(inputStream).use {
            it.readBoolean()
        }
    }
}