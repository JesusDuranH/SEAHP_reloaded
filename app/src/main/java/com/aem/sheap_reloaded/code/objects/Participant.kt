package com.aem.sheap_reloaded.code.objects

import com.aem.sheap_reloaded.code.things.AzureHelper

open class Participant(val user: User,
                       val project: Project,
                       val type: Int
) {
    //
    constructor(): this(User(), Project(), 3)
    constructor(user: User, project: Project): this(user, project, 3)

    fun create(user: User, project: Project, type: Int): Participant{
        //
        val newParticipant = Participant(user, project, type)
        val threadParticipant = Thread {
            AzureHelper().insertParticipant(newParticipant)
        }.apply {
            start()
            join()
        }
        return newParticipant
    }

    fun update(user: User, project: Project, type: Int): Participant{
        //
        val updateParticipant = Participant(user, project, type)
        val threadUpdateParticipant = Thread {
            AzureHelper().updateParticipant(updateParticipant)
        }.apply {
            start()
            join()
        }
        return updateParticipant
    }

    fun delete(user: User, project: Project){
        //
        val deleteParticipant = Participant(user, project)
        val threadDeleteParticipant = Thread {
            AzureHelper().deleteParticipant(deleteParticipant)
        }.apply {
            start()
            join()
        }
    }

    fun byUsers(user: User): List<Participant> {
        //
        var dataList = mutableListOf<Participant>()
        val threadUserList = Thread {
            AzureHelper().getParticipantByUser(user){data ->
                dataList = data.toMutableList()
            }
        }.apply {
            start()
            join()
        }

        return dataList
    }

    fun isAdminInThis(user: User, project: Project): Participant {
        //
        var data = Participant()
        val threadUserList = Thread {
            AzureHelper().getParticipantIsAdminInThis(project, user){getData ->
                data = getData
            }
        }.apply {
            start()
            join()
        }

        return data
    }

    fun listByProject(project: Project): List<Participant>{
        //
        var dataList = mutableListOf<Participant>()
        val threadParticipantList = Thread {
            AzureHelper().getParticipantsByProject(project.idProject){ list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Participant) return false

        if (user != other.user) return false
        if (project != other.project) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 29 * result + user.hashCode()
        result = 29 * result + project.hashCode()
        result = 29 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "\nParticipant($user, $project, $type)"
    }
}