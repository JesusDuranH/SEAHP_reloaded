package com.aem.sheap_reloaded.code.objects

import android.content.Context
import android.util.Log
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.things.AzureHelper
import com.aem.sheap_reloaded.code.things.Save
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

open class User(val user: String,
                val name: String,
                val mail: String,
                val pass: String
): Serializable {
    //
    constructor(): this ("","","", "")
    constructor(user: String): this(user, "","","")
    constructor(user: String, pass: String): this(user,"","",pass)

    private val save = Save()

    fun register(user: String, name: String, mail: String, pass: String): User {
        //
        val newUser = User(user, name, mail, pass)
        val threadInsertUser = Thread {
            AzureHelper().insertUser(newUser)
        }.apply {
            join()
            start()
        }
        return newUser
    }

    fun login(user: String, pass: String, context: Context): Int{
        //
        val ret: Int
        var loginUser = User()
        val threadLogin = Thread {
            AzureHelper().getUserByID(user) { login ->
                loginUser = login
            }
        }.apply {
            start()
            join()
        }
        ret =
            if (loginUser.user != user) 1
            else if (loginUser.pass != pass) 2
            else {
                set(loginUser, context)
                3
            }

        return ret
    }

    fun heExist(user: String): Boolean{
        //
        val ret: Boolean
        var loginUser = User()
        val threadGetUser = Thread {
            AzureHelper().getUserByID(user) { login ->
                loginUser = login
            }
        }.apply {
            start()
            join()
        }
        ret = loginUser != User()
        Log.d("User", "Exist User: $ret")
        return ret
    }

    fun mailExist(mail: String): Boolean{
        //
        val ret: Boolean
        var loginUser = User()

        val threadGetMail = Thread {
            AzureHelper().getUserByMail(mail) { mail ->
                loginUser = mail
            }
        }.apply {
            start()
            join()
        }

        ret = loginUser != User()
        Log.d("User", "Mail Exist: $ret")
        return ret
    }

    fun listAll(): List<User>{
        //
        var listUser = mutableListOf<User>()

        val threadList = Thread {
            AzureHelper().getAllUsers() { list ->
                listUser = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }

        return listUser
    }

    private fun set(setUser: User, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_user_online_tittle),
            setUser.toByteArray(setUser),
            context.getString(R.string.alias_user))
    }

    private fun get(context: Context): User {
        val userOnByte = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_user_online_tittle),
            context.getString(R.string.alias_user))
        return if (userOnByte == null) User()
        else User().toUser(userOnByte)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (user != other.user) return false
        if (name != other.name) return false
        if (mail != other.mail) return false
        if (pass != other.pass) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 29 * result + name.hashCode()
        result = 29 * result + mail.hashCode()
        result = 29 * result + pass.hashCode()
        return result
    }

    fun toByteArray(user: User): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(user)
        return byteArrayOutputStream.toByteArray()
    }

    fun toUser(byteArray: ByteArray): User {
        return ObjectInputStream(ByteArrayInputStream(byteArray)).readObject() as User
    }

    override fun toString(): String {
        //return "Usuario: $user | Nombre: $name | Mail: $mail | Pass: $pass"
        return "User(\"$user\", \"$name\", \"$mail\", \"$pass\")"
    }

}