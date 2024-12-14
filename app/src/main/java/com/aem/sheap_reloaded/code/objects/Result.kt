package com.aem.sheap_reloaded.code.objects

import android.util.Log
import com.aem.sheap_reloaded.code.things.AzureHelper
import com.aem.sheap_reloaded.ui.project.assess.result.progress.ResultGroup

class Result (val id: Long,
              val name: String,
              val participant: Participant,
              val result: Double
) {
    //
    constructor(): this (0,"",Participant(),0.0)

    fun create(id: Long, name: String, participant: Participant, result: Double): Result{
        //
        val newResult = Result(id, name, participant, result)
        val threadParticipant = Thread {
            AzureHelper().insertResult(newResult)
        }.apply {
            start()
            join()
        }
        return newResult
    }

    fun update(id: Long, name: String, participant: Participant, result: Double): Result{
        //
        val updateResult = Result(id, name, participant, result)
        val threadUpdateResult = Thread {
            AzureHelper().updateResultByIDnUser(updateResult)
        }.apply {
            start()
            join()
        }
        return updateResult
    }

    fun byUsersNID(id: Long, participant: Participant): Result {
        //
        var data = Result()
        val threadUserList = Thread {
            AzureHelper().getResult(id, participant.project, participant.user){ result ->
                data = result
            }
        }.apply {
            start()
            join()
        }

        return data
    }

    fun groupByProject(project: Project): List<ResultGroup>{
        //
        var results = emptyList<Result>()
        var groups = emptyList<ResultGroup>()
        val threadUserList = Thread {
            AzureHelper().getAllResultByProject(project) { getResult ->
                results = getResult

                groups =  results.groupBy { it.participant }.map { entry ->
                    ResultGroup(
                        participant = entry.key,
                        results = entry.value
                    )
                }
            }
        }.apply {
            start()
            join()
        }
        return groups
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Result) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (participant != other.participant) return false
        if (result != other.result) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 29 * result + id.hashCode()
        result = 29 * result + name.hashCode()
        result = 29 * result + participant.hashCode()
        result = 29 * result + result.hashCode()
        return result
    }

    override fun toString(): String {
        return "\nResult($id, $name, ${participant.user.user}, ${participant.project.nameProject}, $result)"
    }
}