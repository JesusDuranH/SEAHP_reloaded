package com.aem.sheap_reloaded.code.objects

import android.content.Context
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.things.AzureHelper
import com.aem.sheap_reloaded.code.things.Save
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import kotlin.random.Random

class Alternative(val idAlternative: Long,
                  val nameAlternative: String,
                  val descriptionAlternative: String? = null,
                  idProject: Long,
                  nameProject: String,
                  descriptionProject: String? = null
) : Project(idProject, nameProject, descriptionProject), Serializable {
    //
    private val save = Save()
    constructor(): this (0,"","",0,"",null)
    constructor(id: Long, name: String, desc: String?, idProject: Long): this (id, name, desc,
        idProject,"",null)

    fun create(name: String, desc: String?, project: Project): Alternative{
        //
        var newID: Long
        do {
            newID = Random.nextLong()
            var isFree = Alternative()
            val threadIDAlternative = Thread{
                AzureHelper().getAlternativeByID(newID, project.idProject){ alternative ->
                    isFree = alternative
                }
            }.apply {
                start()
                join()
            }
        } while (isFree != Alternative())
        val newAlternative = Alternative(newID, name, desc,
            project.idProject, project.nameProject, project.descriptionProject)
        AzureHelper().insertAlternative(newAlternative)
        return newAlternative
    }

    fun getByID(id: Long, project: Project): Alternative{
        //
        var alternative = Alternative()
        val threadAlternative = Thread {
            AzureHelper().getAlternativeByID(id, project.idProject) { data ->
                alternative = data
            }
        }.apply {
            start()
            join()
        }
        return alternative
    }

    fun listByProject(project: Project): List<Alternative>{
        //
        var dataList = mutableListOf<Alternative>()

        val threadAlternativeList = Thread {
            AzureHelper().getAlternativesByProject(project){ list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }

        return dataList
    }

    fun setX(setX: Alternative, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_AlternativeX),
            Criteria().toByteArray(setX),
            context.getString(R.string.alias_alternativeX))
    }

    fun getX(context: Context): Alternative{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_AlternativeX),
            context.getString(R.string.alias_alternativeX))
        return if (element != null) Alternative().toAlternative(element)
        else Alternative()
    }

    fun setY(setY: Alternative, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_AlternativeY),
            Criteria().toByteArray(setY),
            context.getString(R.string.alias_alternativeY))
    }

    fun getY(context: Context): Alternative{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_AlternativeY),
            context.getString(R.string.alias_alternativeY))
        return if (element != null) Alternative().toAlternative(element)
        else Alternative()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Alternative) return false

        if (idAlternative != other.idAlternative) return false
        if (nameAlternative != other.nameAlternative) return false
        if (descriptionAlternative != other.descriptionAlternative) return false
        if (idProject != other.idProject) return false
        if (nameProject != other.nameProject) return false
        if (descriptionProject != other.descriptionProject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idAlternative.hashCode()
        result = 29 * result + nameAlternative.hashCode()
        result = 29 * result + descriptionAlternative.hashCode()
        result = 29 * result + idProject.hashCode()
        result = 29 * result + nameProject.hashCode()
        result = 29 * result + descriptionProject.hashCode()
        return result
    }

    fun toByteArray(alternative: Alternative): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(alternative)
        return byteArrayOutputStream.toByteArray()
    }

    fun toAlternative(byteArray: ByteArray): Alternative {
        return ObjectInputStream(ByteArrayInputStream(byteArray)).readObject() as Alternative
    }

    override fun toString(): String {
        return "\nAlternative ($idAlternative, \"$nameAlternative\", \"$descriptionAlternative\", $idProject)"
    }
}