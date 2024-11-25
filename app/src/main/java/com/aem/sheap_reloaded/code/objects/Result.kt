package com.aem.sheap_reloaded.code.objects

import com.aem.sheap_reloaded.code.things.AzureHelper

class Result (val id: Long,
              val name: String,
              val user: User,
              val result: Double
) {
    //
    constructor(): this (0,"",User(),0.0)

    fun create(id: Long, name: String, user: User, result: Double): Result{
        //
        val newResult = Result(id, name, user, result)
        val threadParticipant = Thread {
            AzureHelper().insertResult(newResult)
        }.apply {
            start()
            join()
        }
        return newResult
    }

    fun update(id: Long, name: String, user: User, result: Double): Result{
        //
        val updateResult = Result(id, name, user, result)
        val threadUpdateResult = Thread {
            AzureHelper().updateResultByIDnUser(updateResult)
        }.apply {
            start()
            join()
        }
        return updateResult
    }

    fun byUsersNID(id: Long, user: User): Result {
        //
        var data = Result()
        val threadUserList = Thread {
            AzureHelper().getResultByIDnUser(id, user){ result ->
                data = result
            }
        }.apply {
            start()
            join()
        }

        return data
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Result) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (user != other.user) return false
        if (result != other.result) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 29 * result + id.hashCode()
        result = 29 * result + name.hashCode()
        result = 29 * result + user.hashCode()
        result = 29 * result + result.hashCode()
        return result
    }

    override fun toString(): String {
        return "\nResult($id, $name, ${user.user}, $result)"
    }
}