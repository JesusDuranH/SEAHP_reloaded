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

    fun setUser(setUser: User, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_user_online_tittle),
            setUser.toByteArray(setUser),
            context.getString(R.string.alias_user))
    }

    fun getUser(context: Context): User{
        val userOnByte = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_user_online_tittle),
            context.getString(R.string.alias_user))
        return if (userOnByte == null) User()
        else User().toUser(userOnByte)
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

/*
import com.aem.seahp.R
import com.aem.seahp.code.config.Save
import com.aem.seahp.code.types.Alternative
import com.aem.seahp.code.types.Element
import com.aem.seahp.code.types.Matrix
import com.aem.seahp.code.types.Project

import java.sql.Connection

class ConfigProject {


    fun setElement(setElement: Element, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_Element),
            setElement.toByteArray(setElement),
            context.getString(R.string.alias_element))
    }

    fun setElementB(setElement: Element, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_ElementB),
            setElement.toByteArray(setElement),
            context.getString(R.string.alias_elementB))
    }

    fun getElement(context: Context): Element{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_Element),
            context.getString(R.string.alias_element))
        return if (element != null) Element().toElement(element)
        else Element()
    }

    fun getElementB(context: Context): Element{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_ElementB),
            context.getString(R.string.alias_elementB))
        return if (element != null) Element().toElement(element)
        else Element()
    }



    fun setConnect(connection: Boolean, context: Context){
        save.saveTempFile(context, context.getString(R.string.alias_online), booleanToByteArray(connection))
    }

    fun getConnect(context: Context): Boolean{
        val isConnect = save.readTempFile(context, context.getString(R.string.alias_online))
        Log.d("Files", "Leer archivo Connection: $isConnect")
        return if (isConnect == null)  false
        else byteArrayToBoolean(isConnect)
    }
}
* */