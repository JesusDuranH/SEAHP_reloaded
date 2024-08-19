package com.aem.sheap_reloaded.code.objects

import com.aem.sheap_reloaded.code.things.AzureHelper
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
        } while (isFree == Alternative())
        val newAlternative = Alternative(newID, name, desc,
            project.idProject, project.nameProject, project.descriptionProject)
        AzureHelper().insertAlternative(newAlternative)
        return newAlternative
    }

    fun listProject(project: Project): List<Alternative>{
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
        return "Alternative ($idAlternative, \"$nameAlternative\", \"$descriptionAlternative\", $idProject)"
    }
}