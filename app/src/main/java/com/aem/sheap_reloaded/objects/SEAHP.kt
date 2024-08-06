package com.aem.sheap_reloaded.objects

import android.content.Context
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

class SEAHP() {
    private val save = Save()
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

    fun setMatrix(setMatrix: Matrix, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_matrix),
            setMatrix.toByteArray(setMatrix),
            context.getString(R.string.alias_matrix))
    }

    fun getMatrix(context: Context): Matrix{
        val matrix = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_matrix),
            context.getString(R.string.alias_matrix))
        return if (matrix != null) Matrix().toMatrix(matrix)
        else Matrix()
    }

    fun setProject(setProject: Project, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_project_tittle),
            Project().toByteArray(setProject),
            context.getString(R.string.alias_project))
    }

    fun getProject(context: Context): Project {
        val project = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_project_tittle),
            context.getString(R.string.alias_project))
        return if (project != null) Project().toProject(project)
        else Project()
    }

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

    fun setConnect(connection: Boolean, context: Context){
        save.saveTempFile(context, context.getString(R.string.alias_online), booleanToByteArray(connection))
    }

    fun getConnect(context: Context): Boolean{
        val isConnect = save.readTempFile(context, context.getString(R.string.alias_online))
        Log.d("Files", "Leer archivo Connection: $isConnect")
        return if (isConnect == null)  false
        else byteArrayToBoolean(isConnect)
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
* */