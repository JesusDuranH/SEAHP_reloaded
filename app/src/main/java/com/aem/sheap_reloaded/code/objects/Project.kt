package com.aem.sheap_reloaded.code.objects

import com.aem.sheap_reloaded.code.things.AzureHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import kotlin.random.Random

open class Project( val idProject : Long,
               val nameProject: String,
               val descriptionProject: String? = null
): Serializable {
    //
    constructor(): this(0, "",null)

    fun create(name: String, desc: String?): Project{
        //
        var id: Long
        do {
            //
            id = Random.nextLong()
            var isFree = Project()
            val threadIDProject = Thread {
                AzureHelper().getProjectByID(id){ project ->
                    isFree = project
                }
            }.apply {
                start()
                join()
            }
        } while (isFree == Project())
        val newProject = Project(id, name, desc)
        AzureHelper().insertProject(newProject)
        return newProject
    }

    fun update(id:Long, name: String, desc: String?): Project{
        //
        val changeProject = Project(id, name, desc)
        AzureHelper().updateProjectByID(changeProject)
        return changeProject
    }

    fun getByID(id: Long): Project{
        //
        var dataProject = Project()
        val threadParticipantList = Thread {
            AzureHelper().getProjectByID(id) { data ->
                dataProject = data
            }
        }.apply {
            start()
            join()
        }
        return dataProject
    }

    fun toByteArray(project: Project): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(project)
        return byteArrayOutputStream.toByteArray()
    }

    fun toProject(byteArray: ByteArray): Project {
        return ObjectInputStream(ByteArrayInputStream(byteArray)).readObject() as Project
    }

    override fun toString(): String {
        return "Project($idProject, \"$nameProject\", \"$descriptionProject\")"
    }

}