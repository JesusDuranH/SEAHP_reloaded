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

class Criteria(val idCriteria: Long,
               val nameCriteria: String,
               val descriptionCriteria: String? = null,
               val subCriteria: Long? = null,
               idProject: Long,
               nameProject: String,
               descriptionProject: String? = null
) : Project (idProject, nameProject, descriptionProject), Serializable {
    //
    private val save = Save()

    constructor(): this (0, "", null, null, 0,
        "", null)
    constructor(idCriteria: Long, nameCriteria: String, descriptionCriteria: String?,
                subCriteria: Long?, idProject: Long
    ): this (idCriteria, nameCriteria, descriptionCriteria, subCriteria, idProject, "", null)

    fun create(name: String, desc: String?, idSub: Long?, project: Project): Criteria{
        //
        var newID: Long
        do {
            newID = Random.nextLong()
            var isFree = Criteria()
            val threadIDCriteria = Thread{
                AzureHelper().getCriteriaByID(newID, project.idProject){ criteria ->
                    isFree = criteria
                }
            }.apply {
                start()
                join()
            }
        } while (isFree != Criteria())
        val newCriteria = Criteria(newID, name, desc, idSub,
            project.idProject, project.nameProject, project.descriptionProject)
        AzureHelper().insertCriteria(newCriteria)
        return newCriteria
    }

    fun getByID(idCriteria: Long, project: Long): Criteria{
        var criteria = Criteria()
        val threadIDCriteria = Thread{
            AzureHelper().getCriteriaByID(idCriteria, project){ getCriteria ->
                criteria = getCriteria
            }
        }.apply {
            start()
            join()
        }
        return criteria
    }

    fun listByProject(project: Project): List<Criteria>{
        //
        var dataList = mutableListOf<Criteria>()
        val threadCriteriaList = Thread {
            AzureHelper().getCriteriaByProject(project.idProject){ list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }

        return dataList
    }

    fun setX(setX: Criteria, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_CriteriaX),
            Criteria().toByteArray(setX),
            context.getString(R.string.alias_criteriaX))
    }

    fun getX(context: Context): Criteria{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_CriteriaX),
            context.getString(R.string.alias_criteriaX))
        return if (element != null) Criteria().toCriteria(element)
        else Criteria()
    }

    fun setY(setY: Criteria, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_CriteriaY),
            Criteria().toByteArray(setY),
            context.getString(R.string.alias_criteriaY))
    }

    fun getY(context: Context): Criteria{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_CriteriaY),
            context.getString(R.string.alias_criteriaY))
        return if (element != null) Criteria().toCriteria(element)
        else Criteria()
    }

    fun toByteArray(criteria: Criteria): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(criteria)
        return byteArrayOutputStream.toByteArray()
    }

    fun toCriteria(byteArray: ByteArray): Criteria {
        return ObjectInputStream(ByteArrayInputStream(byteArray)).readObject() as Criteria
    }

    override fun toString(): String {
        return "\nCriteria ($idCriteria, \"$nameCriteria\", \"$descriptionCriteria\", $subCriteria, $idProject)"
    }
}