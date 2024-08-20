package com.aem.sheap_reloaded.code.things

import android.util.Log
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.Properties

class AzureHelper {

    private val TABLE_USER = arrayOf(
        "usuario", //0
        "user_us", //1
        "name_us", //2
        "mail_us", //3
        "pass_us") //4
    private val TABLE_PROYECT = arrayOf(
        "proyecto",
        "project_id",
        "name_pj",
        "description_pj")
    private val TABLE_PARTICIPANT = arrayOf(
        "participante",
        "id_project",
        "us_user",
        "type_us")
    private val TABLE_ALTERNATIVE = arrayOf(
        "alternativa",
        "alternative_id",
        "name_alt",
        "description_alt",
        "id_project")
    private val TABLE_CRITERIA = arrayOf(
        "criterio",
        "criteria_id",
        "name_cr",
        "description_cr",
        "sub_cr",
        "id_project")
    private val TABLE_MATRIX = arrayOf(
        "matriz",       //0
        "matrix_id",    //1
        "name_mat",     //2
        "description_mat",//3
        "row_mat",      //4
        "column_mat",   //5
        "us_user",      //6
        "id_project",   //7
        "us_type")      //8
    private val TABLE_ELEMENT = arrayOf(
        "elemento",
        "id_matrix",
        "id_project",
        "us_user",
        "row_ele",
        "column_ele",
        "name_ele",
        "description_ele",
        "scale_ele")

    fun connect(){
        try {
            Thread {
                try {
                    getConnection()
                    Log.d("DB", "getConnection: Success")
                } catch (ex: SQLException){
                    Log.d("DB", "connect Thread SQLException: " + ex.printStackTrace())
                } catch (e: Exception) {
                    Log.d("DB", "connect Thread Exception: " + e.printStackTrace())
                }
            }.start()
        } catch (ex: SQLException){
            Log.d("DB", "connect SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("DB", "connect Exception: " + e.printStackTrace())
        }
    }

    fun getConnection(): Connection {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
        } catch (ex: ClassNotFoundException) {
            println("Error al registrar el driver de conexion: $ex")
        }

        //Azure
        //val url = "jdbc:jtds:sqlserver://seahp-server.database.windows.net:1433;DataBaseName=seahp;
        // user=Jadmin@seahp-server;password=4*63YWz93YPY8^j^;ssl=request;encrypt=true;trustServerCertificate=false;
        // hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
        val url = "jdbc:jtds:sqlserver://seahp-server.database.windows.net:1433;DataBaseName=seahp;"
        val properties = Properties()
        properties.setProperty("encrypt", "true")
        properties.setProperty("trustServerCertificate", "false")
        properties.setProperty("hostNameInCertificate", "*.database.windows.net") // Cambia esto por el nombre de tu servidor
        properties.setProperty("loginTimeout", "30")
        properties.setProperty("user", "Jadmin@seahp-server")
        properties.setProperty("password", "4*63YWz93YPY8^j^")
        properties.setProperty("ssl", "request")
        properties.setProperty("sslProtocol", "TLSv1.2")
        properties.setProperty("loginTimeout", "30")
        //properties.setProperty("enabledTLSProtocols", "TLSv1.2")
        return DriverManager.getConnection(url, properties)
    }

    fun insertUser(user: User){
        //
        val sql = "INSERT INTO ${TABLE_USER[0]} (${TABLE_USER[1]},${TABLE_USER[2]},${TABLE_USER[3]},${TABLE_USER[4]}) " +
                "VALUES (?, ?, ?, ?)"
        Log.d("AzureDB", "Insert User: $sql")
        try {
            val conn = getConnection()
            val statement: PreparedStatement = conn.prepareStatement(sql)
            statement.setString(1, user.user)
            statement.setString(2, user.name)
            statement.setString(3, user.mail)
            statement.setString(4, user.pass)
            statement.executeUpdate()
            statement.close()
            Log.d("AzureDB", "insertUser: $user")
        } catch (ex: SQLException){
            Log.d("AzureDB", "insertUser SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "insertUser Exception: " + e.printStackTrace())
        }
    }

    fun getAllUsers(callback: (List<User>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_USER[0]}"
        Log.d("AzureDB", "getAllUsers: $sql")
        val users = mutableListOf<User>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.executeQuery().use { rs ->
                        while (rs.next()) {
                            val user = rs.getString(TABLE_USER[1])
                            val name = rs.getString(TABLE_USER[2])
                            val mail = rs.getString(TABLE_USER[3])
                            val searchUser = User(user, name, mail, "")
                            users.add(searchUser)
                        }
                        Log.d("AzureDB", "getAllUsers: $users")
                        callback(users)
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getAllUsers SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getAllUsers Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }

    fun getUserByID(user: String, callback: (User) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_USER[0]} WHERE ${TABLE_USER[1]} = ?"
        Log.d("AzureDB", "getUserByID: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setString(1, user)
                    statement.executeQuery().use { rs ->
                        var loginUser = User()
                        if (rs.next()) {
                            val checkUser = rs.getString(TABLE_USER[1])
                            val name = rs.getString(TABLE_USER[2])
                            val mail = rs.getString(TABLE_USER[3])
                            val pass = rs.getString(TABLE_USER[4])
                            loginUser = User(checkUser, name, mail, pass)
                        }
                        Log.d("AzureDB", "getUserByID: $loginUser")
                        callback(loginUser)
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getUserByUser SQLException: " + ex.printStackTrace())
            callback(User())
        } catch (e: Exception) {
            Log.d("AzureDB", "getUserByUser Exception: " + e.printStackTrace())
            callback(User())
        }
    }

    fun getUserByMail(mail: String, callback: (User) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_USER[0]} WHERE ${TABLE_USER[3]} = ?"
        Log.d("AzureDB", "getUserByMail: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setString(1, mail)
                    statement.executeQuery().use { rs ->
                        var loginUser = User()
                        if (rs.next()) {
                            val user = rs.getString(TABLE_USER[1])
                            val name = rs.getString(TABLE_USER[2])
                            val getMail = rs.getString(TABLE_USER[3])
                            val pass = ""
                            loginUser = User(user, name, getMail, pass)
                        }
                        Log.d("AzureDB", "getUserByMail: $loginUser")
                        callback(loginUser)
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getUserByMail SQLException: " + ex.printStackTrace())
            callback(User())
        } catch (e: Exception) {
            Log.d("AzureDB", "getUserByMail Exception: " + e.printStackTrace())
            callback(User())
        }
    }

    fun insertProject(project: Project){
        //
        val sql = "INSERT INTO ${TABLE_PROYECT[0]} (${TABLE_PROYECT[1]},${TABLE_PROYECT[2]},${TABLE_PROYECT[3]}) " +
                "VALUES (?, ?, ?)"
        Log.d("AzureDB", "insertProject: $sql")
        try {
            val conn = getConnection()
            val statement: PreparedStatement = conn.prepareStatement(sql)
            statement.setLong(1, project.idProject)
            statement.setString(2, project.nameProject)
            statement.setString(3, project.descriptionProject)
            statement.executeUpdate()
            statement.close()
            Log.d("AzureDB", "insertProject: $project")
        } catch (ex: SQLException){
            Log.d("AzureDB", "insertProject SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "insertProject Exception: " + e.printStackTrace())
        }
    }

    fun getProjectByID(project: Long, callback: (Project) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_PROYECT[0]} WHERE ${TABLE_PROYECT[1]} = ?"
        //
        Log.d("AzureDB", "getProjectByID: $sql")
        //
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, project)
                    statement.executeQuery().use { rs ->
                        var searchProject = Project()
                        if (rs.next()) {
                            val id = rs.getLong(TABLE_PROYECT[1])
                            val name = rs.getString(TABLE_PROYECT[2])
                            val desc = rs.getString(TABLE_PROYECT[3])
                            searchProject = Project(id, name, desc)
                        }
                        Log.d("AzureDB", "getProjectByID: $searchProject")
                        callback(searchProject)
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getProjectByID SQLException: " + ex.printStackTrace())
            callback(Project())
        } catch (e: Exception) {
            Log.d("AzureDB", "getProjectByID Exception: " + e.printStackTrace())
            callback(Project())
        }
    }

    fun updateProjectByID(project: Project){
        //
        val sql = "UPDATE ${TABLE_PROYECT[0]} SET ${TABLE_PROYECT[2]} = ? , ${TABLE_PROYECT[3]} = ? " +
                "WHERE ${TABLE_PROYECT[1]} = ?"
        Log.d("AzureDB", "updateProjectByID: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setString(1, project.nameProject)
                    statement.setString(2, project.descriptionProject)
                    statement.setLong(3, project.idProject)
                    val i = statement.executeUpdate()
                    Log.d("AzureDB", "updateProjectByID Row: $i")
                    Log.d("AzureDB", "updateProjectByID: $project")
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "updateProjectByID SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "updateProjectByID Exception: " + e.printStackTrace())
        }
    }

    fun insertParticipant(participant: Participant){
        //
        val sql = "INSERT INTO ${TABLE_PARTICIPANT[0]} (${TABLE_PARTICIPANT[1]},${TABLE_PARTICIPANT[2]},${TABLE_PARTICIPANT[3]}) " +
                "VALUES (?, ?, ?)"
        Log.d("AzureDB", "insertParticipant: $sql")
        try {
            val conn = getConnection()
            val statement: PreparedStatement = conn.prepareStatement(sql)
            statement.setLong(1, participant.project.idProject)
            statement.setString(2, participant.user.user)
            statement.setInt(3, participant.type)
            statement.executeUpdate()
            statement.close()
            Log.d("AzureDB", "insertParticipant: $participant")
        } catch (ex: SQLException){
            Log.d("AzureDB", "insertParticipant SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "insertParticipant Exception: " + e.printStackTrace())
        }
    }

    fun updateParticipant(participant: Participant){
        //
        val sql = "UPDATE ${TABLE_PARTICIPANT[0]} SET ${TABLE_PARTICIPANT[3]} = ? WHERE ${TABLE_PARTICIPANT[1]} = ? AND ${TABLE_PARTICIPANT[2]} = ?"
        Log.d("AzureDB", "updateParticipant: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setInt(1, participant.type)
                    statement.setLong(2, participant.project.idProject)
                    statement.setString(3, participant.user.user)
                    val i = statement.executeUpdate()
                    Log.d("AzureDB", "updateParticipant Row: $i")
                    Log.d("AzureDB", "updateParticipant: $participant")
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "updateParticipant SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "updateParticipant Exception: " + e.printStackTrace())
        }
    }

    fun deleteParticipant(participant: Participant){
        //
        val sql = "DELETE FROM ${TABLE_PARTICIPANT[0]} WHERE ${TABLE_PARTICIPANT[1]} = ? AND ${TABLE_PARTICIPANT[2]} = ?"
        Log.d("AzureDB", "deleteParticipant: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, participant.project.idProject)
                    statement.setString(2, participant.user.user)
                    val i = statement.executeUpdate()
                    Log.d("AzureDB", "deleteParticipant Row: $i")
                    Log.d("AzureDB", "deleteParticipant: $participant")
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "deleteParticipant SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "deleteParticipant Exception: " + e.printStackTrace())
        }
    }

    fun getParticipantByUser(user: User, callback: (List<Participant>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_PARTICIPANT[0]} WHERE ${TABLE_PARTICIPANT[2]} = ?"
        Log.d("AzureDB", "getParticipantByUser: $sql")
        val participants = mutableListOf<Participant>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getUserByID(user.user){getUser ->
                        statement.setString(1, getUser.user)
                        statement.executeQuery().use { rs ->
                            while (rs.next()) {
                                val idProject = rs.getLong(TABLE_PARTICIPANT[1])
                                val type = rs.getInt(TABLE_PARTICIPANT[3])
                                getProjectByID(idProject){getProject ->
                                    val searchParticipant = Participant(getUser, getProject, type)
                                    participants.add(searchParticipant)
                                }
                            }
                        }
                        Log.d("AzureDB", "getParticipantByUser: $participants")
                        callback(participants)
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getParticipantByUser SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getParticipantByUser Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }

    //A falta de un mejor nombre, se queda asi
    //Checa si el usuario en el proyecto es administrador o no
    fun getParticipantIsAdminInThis(project: Project, user: User, callback: (Participant) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_PARTICIPANT[0]} " +
                "WHERE ${TABLE_PARTICIPANT[1]} = ? AND ${TABLE_PARTICIPANT[2]} = ? AND ${TABLE_PARTICIPANT[3]} = 2"
        Log.d("AzureDB", "getParticipantIsAdminInThis: $sql")
        var participant = Participant()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, project.idProject)
                    statement.setString(2, user.user)
                    statement.executeQuery().use { rs ->
                        if (rs.next()) {
                            val type = rs.getInt(TABLE_PARTICIPANT[3])
                            participant = Participant(user, project, type)
                            Log.d("AzureDB", "getParticipantIsAdminInThis: $participant")
                        }
                        callback(participant)
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getParticipantIsAdminInThis SQLException: " + ex.printStackTrace())
            callback(Participant())
        } catch (e: Exception) {
            Log.d("AzureDB", "getParticipantIsAdminInThis Exception: " + e.printStackTrace())
            callback(Participant())
        }
    }

    fun getParticipantsByProject(project: Long, callback: (List<Participant>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_PARTICIPANT[0]} WHERE ${TABLE_PARTICIPANT[1]} = ?"
        Log.d("AzureDB", "getParticipantsByProject: $sql")
        val participants = mutableListOf<Participant>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project){getProject ->
                        statement.setLong(1, getProject.idProject)
                        statement.executeQuery().use { rs ->
                            while (rs.next()) {
                                val idUser = rs.getString(TABLE_PARTICIPANT[2])
                                val type = rs.getInt(TABLE_PARTICIPANT[3])
                                getUserByID(idUser){getUser ->
                                    val searchParticipant = Participant(getUser, getProject, type)
                                    participants.add(searchParticipant)
                                }
                            }
                        }
                        Log.d("AzureDB", "getParticipantsByProject: $participants")
                        callback(participants)
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getParticipantsByProject SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getParticipantsByProject Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }

    fun insertCriteria(criteria: Criteria){
        //
        val sql = "INSERT INTO ${TABLE_CRITERIA[0]} (${TABLE_CRITERIA[1]},${TABLE_CRITERIA[2]}," +
                "${TABLE_CRITERIA[3]},${TABLE_CRITERIA[4]},${TABLE_CRITERIA[5]}) " +
                "VALUES (?, ?, ?, ?, ?)"
        Log.d("AzureDB", "insertCriteria: $sql")
        try {
            val conn = getConnection()
            val statement: PreparedStatement = conn.prepareStatement(sql)
            statement.setLong(1, criteria.idCriteria)
            statement.setString(2, criteria.nameCriteria)
            statement.setString(3, criteria.descriptionCriteria)
            if (criteria.subCriteria != null) statement.setLong(4, criteria.subCriteria)
            else statement.setObject(4, null)
            statement.setLong(5, criteria.idProject)
            statement.executeUpdate()
            statement.close()
            Log.d("AzureDB", "insertCriteria: $criteria")
        } catch (ex: SQLException){
            Log.d("AzureDB", "insertCriteria SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "insertCriteria Exception: " + e.printStackTrace())
        }
    }

    fun getCriteriaByID(criteria: Long, project: Long, callback: (Criteria) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_CRITERIA[0]} WHERE ${TABLE_CRITERIA[1]} = ? AND ${TABLE_CRITERIA[5]} = ?"
        Log.d("AzureDB", "getCriteriaByID: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project){getProject ->
                        statement.setLong(1, criteria)
                        statement.setLong(2, getProject.idProject)
                        statement.executeQuery().use { rs ->
                            var searchCriteria = Criteria()
                            if (rs.next()) {
                                val id = rs.getLong(TABLE_CRITERIA[1])
                                val name = rs.getString(TABLE_CRITERIA[2])
                                val desc = rs.getString(TABLE_CRITERIA[3])
                                val sub = rs.getLong(TABLE_CRITERIA[4])
                                searchCriteria = Criteria(id, name, desc, sub,
                                    getProject.idProject, getProject.nameProject, getProject.descriptionProject)
                            }
                            Log.d("AzureDB", "getCriteriaByID: $searchCriteria")
                            callback(searchCriteria)
                        }
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getCriteriaByID SQLException: " + ex.printStackTrace())
            callback(Criteria())
        } catch (e: Exception) {
            Log.d("AzureDB", "getCriteriaByID Exception: " + e.printStackTrace())
            callback(Criteria())
        }
    }

    fun getCriteriaByProject(project: Long, callback: (List<Criteria>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_CRITERIA[0]} WHERE ${TABLE_CRITERIA[5]} = ?"
        Log.d("AzureDB", "getCriteriaByProject: $sql")
        val criteria = mutableListOf<Criteria>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project){getProject ->
                        statement.setLong(1, getProject.idProject)
                        statement.executeQuery().use { rs ->
                            var searchCriteria: Criteria
                            while (rs.next()) {
                                val id = rs.getLong(TABLE_CRITERIA[1])
                                val name = rs.getString(TABLE_CRITERIA[2])
                                val desc = rs.getString(TABLE_CRITERIA[3])
                                val sub = rs.getLong(TABLE_CRITERIA[4])
                                searchCriteria = Criteria(id, name, desc, sub,
                                    getProject.idProject, getProject.nameProject, getProject.descriptionProject)
                                criteria.add(searchCriteria)
                            }
                            Log.d("AzureDB", "getCriteriaByProject: $criteria")
                            callback(criteria)
                        }
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getCriteriaByProject SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getCriteriaByProject Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }

    fun insertAlternative(alternative: Alternative){
        //
        val sql = "INSERT INTO ${TABLE_ALTERNATIVE[0]} (${TABLE_ALTERNATIVE[1]},${TABLE_ALTERNATIVE[2]},${TABLE_ALTERNATIVE[3]},${TABLE_ALTERNATIVE[4]}) " +
                "VALUES (?, ?, ?, ?)"
        Log.d("AzureDB", "insertAlternative: $sql")
        try {
            val conn = getConnection()
            val statement: PreparedStatement = conn.prepareStatement(sql)
            statement.setLong(1, alternative.idAlternative)
            statement.setString(2, alternative.nameAlternative)
            statement.setString(3, alternative.descriptionAlternative)
            statement.setLong(4, alternative.idProject)
            statement.executeUpdate()
            statement.close()
            Log.d("AzureDB", "insertAlternative: $alternative")
        } catch (ex: SQLException){
            Log.d("AzureDB", "insertAlternative SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "insertAlternative Exception: " + e.printStackTrace())
        }
    }

    fun getAlternativeByID(alternative: Long, project: Long, callback: (Alternative) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ALTERNATIVE[0]} WHERE ${TABLE_ALTERNATIVE[1]} = ? AND ${TABLE_ALTERNATIVE[4]} = ?"
        Log.d("AzureDB", "getAlternativeByID: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project){getProject ->
                        statement.setLong(1, alternative)
                        statement.setLong(2, getProject.idProject)
                        statement.executeQuery().use { rs ->
                            var searchAlternative = Alternative()
                            if (rs.next()) {
                                val id = rs.getLong(TABLE_ALTERNATIVE[1])
                                val name = rs.getString(TABLE_ALTERNATIVE[2])
                                val desc = rs.getString(TABLE_ALTERNATIVE[3])
                                searchAlternative = Alternative(id, name, desc,
                                    getProject.idProject, getProject.nameProject, getProject.descriptionProject)
                            }
                            Log.d("AzureDB", "getAlternativeByID: $searchAlternative")
                            callback(searchAlternative)
                        }
                    }
                }
            }
        } catch (ex: SQLException) {
            Log.d("AzureDB", "getAlternativeByID SQLException: " + ex.printStackTrace())
            callback(Alternative())
        } catch (e: Exception) {
            Log.d("AzureDB", "getAlternativeByID Exception: " + e.printStackTrace())
            callback(Alternative())
        }
    }

    fun getAlternativesByProject(project: Project, callback: (List<Alternative>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ALTERNATIVE[0]} WHERE ${TABLE_ALTERNATIVE[4]} = ?"
        Log.d("AzureDB", "getAlternativesByProject: $sql")
        val alternatives = mutableListOf<Alternative>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project.idProject){getProject ->
                        statement.setLong(1, getProject.idProject)
                        statement.executeQuery().use { rs ->
                            var searchAlternative: Alternative
                            while (rs.next()) {
                                val id = rs.getLong(TABLE_ALTERNATIVE[1])
                                val name = rs.getString(TABLE_ALTERNATIVE[2])
                                val desc = rs.getString(TABLE_ALTERNATIVE[3])
                                searchAlternative = Alternative(id, name, desc,
                                    getProject.idProject, getProject.nameProject, getProject.descriptionProject)
                                alternatives.add(searchAlternative)
                            }
                            Log.d("AzureDB", "getAlternativesByProject: $alternatives")
                            callback(alternatives)
                        }
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getAlternativesByProject SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getAlternativesByProject Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }

    fun insertMatrix(matrix: Matrix){
        //
        val sql = "INSERT INTO ${TABLE_MATRIX[0]} (${TABLE_MATRIX[1]},${TABLE_MATRIX[2]}," +
                "${TABLE_MATRIX[3]},${TABLE_MATRIX[4]},${TABLE_MATRIX[5]},${TABLE_MATRIX[6]}," +
                "${TABLE_MATRIX[7]},${TABLE_MATRIX[8]}) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        Log.d("AzureDB", "insertMatrix: $sql")
        try {
            val conn = getConnection()
            val statement: PreparedStatement = conn.prepareStatement(sql)
            statement.setLong(1, matrix.idMatrix)
            statement.setString(2, matrix.nameMatrix)
            statement.setString(3, matrix.descriptionMatrix)
            statement.setInt(4, matrix.rowMax)
            statement.setInt(5, matrix.columnMax)
            statement.setString(6, matrix.user.user)
            statement.setLong(7, matrix.project.idProject)
            statement.setInt(8, matrix.type)
            statement.executeUpdate()
            statement.close()
            Log.d("AzureDB", "insertMatrix: $matrix")
        } catch (ex: SQLException){
            Log.d("AzureDB", "insertMatrix SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "insertMatrix Exception: " + e.printStackTrace())
        }
    }

    fun getMatrixListByID(project: Long, matrix: Long, callback: (List<Matrix>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_MATRIX[0]} WHERE ${TABLE_MATRIX[1]} = ? AND ${TABLE_MATRIX[7]} = ?"
        Log.d("AzureDB", "getMatrixListByID: $sql")
        val list = mutableListOf<Matrix>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project){getProject ->
                        statement.setLong(1, matrix)
                        statement.setLong(2, getProject.idProject)
                        statement.executeQuery().use { rs ->
                            var searchMatrix = Matrix()
                            while (rs.next()) {
                                val id = rs.getLong(TABLE_MATRIX[1])
                                val name = rs.getString(TABLE_MATRIX[2])
                                val desc = rs.getString(TABLE_MATRIX[3])
                                val row = rs.getInt(TABLE_MATRIX[4])
                                val column = rs.getInt(TABLE_MATRIX[5])
                                val idUser = rs.getString(TABLE_MATRIX[6])
                                val type = rs.getInt(TABLE_MATRIX[8])
                                getUserByID(idUser){user ->
                                    searchMatrix = Matrix(id, name, desc, row, column, user, getProject, type)
                                }
                                list.add(searchMatrix)
                            }
                            Log.d("AzureDB", "getMatrixListByID $list")
                            callback(list)
                        }
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getMatrixListByID SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getMatrixListByID Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }

    fun getMatrixByIDnUser(project: Project, matrix: Matrix, user: User, callback: (Matrix) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_MATRIX[0]} WHERE ${TABLE_MATRIX[1]} = ? AND ${TABLE_MATRIX[6]} = ?" +
                "${TABLE_MATRIX[7]} AND = ?"
        Log.d("AzureDB", "getMatrixByIDnUser: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project.idProject){getProject ->
                        getUserByID(user.user){getUser ->
                            statement.setLong(1, matrix.idMatrix)
                            statement.setString(2, getUser.user)
                            statement.setLong(3, getProject.idProject)
                            statement.executeQuery().use { rs ->
                                var searchMatrix = Matrix()
                                while (rs.next()) {
                                    val id = rs.getLong(TABLE_MATRIX[1])
                                    val name = rs.getString(TABLE_MATRIX[2])
                                    val desc = rs.getString(TABLE_MATRIX[3])
                                    val row = rs.getInt(TABLE_MATRIX[4])
                                    val column = rs.getInt(TABLE_MATRIX[5])
                                    val type = rs.getInt(TABLE_MATRIX[8])
                                    searchMatrix = Matrix(id, name, desc, row, column, getUser, getProject, type)
                                }
                                Log.d("AzureDB", "getMatrixByIDnUser $searchMatrix")
                                callback(searchMatrix)
                            }
                        }
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getMatrixByIDnUser SQLException: " + ex.printStackTrace())
            callback(Matrix())
        } catch (e: Exception) {
            Log.d("AzureDB", "getMatrixByIDnUser Exception: " + e.printStackTrace())
            callback(Matrix())
        }
    }

    fun getAllMatrixByProject(project: Project, callback: (List<Matrix>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_MATRIX[0]} WHERE ${TABLE_MATRIX[7]} = ?"
        Log.d("AzureDB", "getMatrixByProject: $sql")
        val matrixs = mutableListOf<Matrix>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getProjectByID(project.idProject){getProject ->
                        statement.setLong(1, getProject.idProject)
                        statement.executeQuery().use { rs ->
                            var searchMatrix: Matrix
                            while (rs.next()) {
                                val id = rs.getLong(TABLE_MATRIX[1])
                                val name = rs.getString(TABLE_MATRIX[2])
                                val desc = rs.getString(TABLE_MATRIX[3])
                                val row = rs.getInt(TABLE_MATRIX[4])
                                val column = rs.getInt(TABLE_MATRIX[5])
                                val idUser = rs.getString(TABLE_MATRIX[6])
                                val type = rs.getInt(TABLE_MATRIX[8])
                                getUserByID(idUser){getUser ->
                                    searchMatrix = Matrix(id, name, desc, row, column, getUser, project, type)
                                    matrixs.add(searchMatrix)
                                    Log.d("AzureDB", searchMatrix.toString())
                                }
                            }
                            Log.d("AzureDB", "getMatrixByProject: $matrixs")
                            callback(matrixs)
                        }
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getMatrixByProject SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getMatrixByProject Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }

    fun insertElement(element: Element){
        //
        val sql = "INSERT INTO ${TABLE_ELEMENT[0]} (${TABLE_ELEMENT[1]},${TABLE_ELEMENT[2]}," +
                "${TABLE_ELEMENT[3]},${TABLE_ELEMENT[4]},${TABLE_ELEMENT[5]},${TABLE_ELEMENT[6]}," +
                "${TABLE_ELEMENT[7]},${TABLE_ELEMENT[8]}) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        Log.d("AzureDB", "insertElement: $sql")
        try {
            val conn = getConnection()
            val statement: PreparedStatement = conn.prepareStatement(sql)
            statement.setLong(1, element.idMatrix)
            statement.setLong(2, element.project.idProject)
            statement.setString(3, element.user.user)
            statement.setInt(4, element.yElement)
            statement.setInt(5, element.xElement)
            statement.setString(6, element.nameElement)
            statement.setString(7, element.descriptionElement)
            if (element.scaleElement == null) statement.setDouble(8, 0.0)
            else statement.setDouble(8, element.scaleElement)
            statement.executeUpdate()
            statement.close()
            Log.d("AzureDB", "insertMatrix: $element")
        } catch (ex: SQLException){
            Log.d("AzureDB", "insertElement SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "insertElement Exception: " + e.printStackTrace())
        }
    }

    fun updateElementByID(element: Element){
        //
        val sql = "UPDATE ${TABLE_ELEMENT[0]} SET ${TABLE_ELEMENT[8]} = ? " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[3]} = ? AND ${TABLE_ELEMENT[4]} = ? AND ${TABLE_ELEMENT[5]} = ?"
        Log.d("AzureDB", "updateElementByID: $sql")
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setDouble(1, element.scaleElement!!)
                    statement.setLong(2, element.idMatrix)
                    statement.setLong(3, element.project.idProject)
                    statement.setString(4, element.user.user)
                    statement.setInt(5, element.yElement)
                    statement.setInt(6, element.xElement)
                    val i = statement.executeUpdate()
                    Log.d("AzureDB", "updateElementByID Row: $i")
                    Log.d("AzureDB", "updateElementByID: $element")
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "updateElementByID SQLException: " + ex.printStackTrace())
        } catch (e: Exception) {
            Log.d("AzureDB", "updateElementByID Exception: " + e.printStackTrace())
        }
    }

    fun getAllElementsOnMatrixByUser(matrix: Matrix, project: Project, user: User, callback: (List<Element>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ELEMENT[0]} WHERE ${TABLE_ELEMENT[1]} = ? " +
                "AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[3]} = ?"
        Log.d("AzureDB", "getAllElementsOnMatrixByUser: $sql")
        val elements = mutableListOf<Element>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    getMatrixByIDnUser(project, matrix, user){getMatrix ->
                        statement.setLong(1, getMatrix.idMatrix)
                        statement.setLong(2, getMatrix.project.idProject)
                        statement.setString(3, getMatrix.user.user)
                        statement.executeQuery().use { rs ->
                            var searchElement: Element
                            while (rs.next()) {
                                val row = rs.getInt(TABLE_ELEMENT[4])
                                val col = rs.getInt(TABLE_ELEMENT[5])
                                val name = rs.getString(TABLE_ELEMENT[6])
                                val desc = rs.getString(TABLE_ELEMENT[7])
                                val scale = rs.getDouble(TABLE_ELEMENT[8])
                                searchElement = Element(col, row, name, desc, scale,
                                    getMatrix.idMatrix, getMatrix.nameMatrix, getMatrix.descriptionMatrix,
                                    getMatrix.rowMax, getMatrix.columnMax, getMatrix.user, getMatrix.project,
                                    getMatrix.type)
                                elements.add(searchElement)
                            }
                            Log.d("AzureDB", "getMatrixByID: $elements")
                            callback(elements)
                        }
                    }
                }
            }
        } catch (ex: SQLException){
            Log.d("AzureDB", "getMatrixByID SQLException: " + ex.printStackTrace())
            callback(mutableListOf())
        } catch (e: Exception) {
            Log.d("AzureDB", "getMatrixByID Exception: " + e.printStackTrace())
            callback(mutableListOf())
        }
    }
}

/*
import com.aem.seahp.code.types.Alternative
import com.aem.seahp.code.types.Criteria
import com.aem.seahp.code.types.Element
import com.aem.seahp.code.types.Matrix
import com.aem.seahp.code.types.Participant


class AzureHelper {
    //

    fun getAllAssessElementsOnMatrixAllUsers(matrix: Matrix, project: Project, callback: (List<Element>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? "
        Log.d("DB", sql)
        val elements = mutableListOf<Element>()
        try {
            getMatrixByID(project.idProject, matrix.idMatrix){mat ->
                getProjectByID(project.idProject){proj ->
                    getConnection().use { conn ->
                        conn.prepareStatement(sql).use { statement ->
                            statement.setLong(1, matrix.idMatrix)
                            statement.setLong(2, project.idProject)
                            statement.executeQuery().use { rs ->
                                var searchElement: Element
                                while (rs.next()) {
                                    val id_mat = rs.getLong(TABLE_ELEMENT[1])
                                    val id_pro = rs.getLong(TABLE_ELEMENT[2])
                                    val us_us = rs.getString(TABLE_ELEMENT[3])
                                    val row_el = rs.getInt(TABLE_ELEMENT[4])
                                    val col_ele = rs.getInt(TABLE_ELEMENT[5])
                                    val name = rs.getString(TABLE_ELEMENT[6])
                                    val desc = rs.getString(TABLE_ELEMENT[7])
                                    val scale = rs.getDouble(TABLE_ELEMENT[8])

                                    getUserByUser(us_us){getUser ->
                                        //
                                        searchElement = Element(col_ele, row_el, name, desc, scale,
                                            mat.idMatrix, mat.nameMatrix, mat.descriptionMatrix,
                                            mat.rowMax, mat.columnMax, getUser, proj,
                                            matrix.type)
                                        elements.add(searchElement)
                                        Log.d("DB", searchElement.toString())
                                    }
                                }
                            }
                            callback(elements)
                        }
                    }
                }
            }

        } catch (e: SQLException) {
            e.printStackTrace()
            callback(mutableListOf())
        }
    }

    fun getRowElementsOnMatrix(matrix: Matrix, project: Project, callback: (List<Element>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[5]} = 0 AND ${TABLE_ELEMENT[4]} != 0"
        Log.d("DB", sql)
        val elements = mutableListOf<Element>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, matrix.idMatrix)
                    statement.setLong(2, project.idProject)
                    statement.executeQuery().use { rs ->
                        var searchElement: Element
                        while (rs.next()) {
                            val id_mat = rs.getLong(TABLE_ELEMENT[1])
                            val id_pro = rs.getLong(TABLE_ELEMENT[2])
                            val us_us = rs.getString(TABLE_ELEMENT[3])
                            val row_el = rs.getInt(TABLE_ELEMENT[4])
                            val col_ele = rs.getInt(TABLE_ELEMENT[5])
                            val name = rs.getString(TABLE_ELEMENT[6])
                            val desc = rs.getString(TABLE_ELEMENT[7])
                            val scale = rs.getDouble(TABLE_ELEMENT[8])

                            getMatrixByID(id_pro, id_mat){matrix ->
                                //
                                searchElement = Element(col_ele, row_el, name, desc, scale,
                                    matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
                                    matrix.rowMax, matrix.columnMax, matrix.user, matrix.project,
                                    matrix.type)
                                elements.add(searchElement)
                                Log.d("DB", searchElement.toString())
                            }
                        }
                        callback(elements)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            callback(mutableListOf())
        }
    }

    fun getOneElementOfCreateOnMatrix(matrix: Matrix, project: Project, user: User, callback: (List<Element>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[3]} = ? " +
                "AND ${TABLE_ELEMENT[4]} = 0 AND ${TABLE_ELEMENT[5]} = 0"
        Log.d("DB", sql)
        val elements = mutableListOf<Element>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, matrix.idMatrix)
                    statement.setLong(2, project.idProject)
                    statement.setString(3, user.user)
                    statement.executeQuery().use { rs ->
                        var searchElement: Element
                        while (rs.next()) {
                            val id_mat = rs.getLong(TABLE_ELEMENT[1])
                            val id_pro = rs.getLong(TABLE_ELEMENT[2])
                            val us_us = rs.getString(TABLE_ELEMENT[3])
                            val row_el = rs.getInt(TABLE_ELEMENT[4])
                            val col_ele = rs.getInt(TABLE_ELEMENT[5])
                            val name = rs.getString(TABLE_ELEMENT[6])
                            val desc = rs.getString(TABLE_ELEMENT[7])
                            val scale = rs.getDouble(TABLE_ELEMENT[8])

                            getMatrixByID(id_pro, id_mat){matrix ->
                                //
                                searchElement = Element(col_ele, row_el, name, desc, scale,
                                    matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
                                    matrix.rowMax, matrix.columnMax, matrix.user, matrix.project,
                                    matrix.type)
                                elements.add(searchElement)
                                Log.d("DB", searchElement.toString())
                            }
                        }
                        callback(elements)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            callback(mutableListOf())
        }
    }

    fun getOneElementToEvaluateOnMatrix(matrix: Matrix, project: Project, user: User,
                                        xElement: Element, yElement: Element, callback: (List<Element>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[3]} = ? " +
                "AND ${TABLE_ELEMENT[4]} = ? AND ${TABLE_ELEMENT[5]} = ?"
        Log.d("DB", sql)
        val elements = mutableListOf<Element>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, matrix.idMatrix)
                    statement.setLong(2, project.idProject)
                    statement.setString(3, user.user)
                    statement.setInt(4, yElement.yElement)
                    statement.setInt(5, xElement.xElement)
                    statement.executeQuery().use { rs ->
                        var searchElement: Element
                        while (rs.next()) {
                            val id_mat = rs.getLong(TABLE_ELEMENT[1])
                            val id_pro = rs.getLong(TABLE_ELEMENT[2])
                            val us_us = rs.getString(TABLE_ELEMENT[3])
                            val row_el = rs.getInt(TABLE_ELEMENT[4])
                            val col_ele = rs.getInt(TABLE_ELEMENT[5])
                            val name = rs.getString(TABLE_ELEMENT[6])
                            val desc = rs.getString(TABLE_ELEMENT[7])
                            val scale = rs.getDouble(TABLE_ELEMENT[8])

                            getMatrixByID(id_pro, id_mat){matrix ->
                                //
                                searchElement = Element(col_ele, row_el, name, desc, scale,
                                    matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
                                    matrix.rowMax, matrix.columnMax, matrix.user, matrix.project,
                                    matrix.type)
                                elements.add(searchElement)
                                Log.d("DB", searchElement.toString())
                            }
                        }
                        callback(elements)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            callback(mutableListOf())
        }
    }

    fun getRowElementsEvaluationOnMatrix(matrix: Matrix, project: Project, user: User, element: Element, callback: (List<Element>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[3]} = ? AND ${TABLE_ELEMENT[4]} = ${element.yElement}"
        Log.d("DB", sql)
        val elements = mutableListOf<Element>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, matrix.idMatrix)
                    statement.setLong(2, project.idProject)
                    statement.setString(3, user.user)
                    statement.executeQuery().use { rs ->
                        var searchElement: Element
                        while (rs.next()) {
                            val id_mat = rs.getLong(TABLE_ELEMENT[1])
                            val id_pro = rs.getLong(TABLE_ELEMENT[2])
                            val us_us = rs.getString(TABLE_ELEMENT[3])
                            val row_el = rs.getInt(TABLE_ELEMENT[4])
                            val col_ele = rs.getInt(TABLE_ELEMENT[5])
                            val name = rs.getString(TABLE_ELEMENT[6])
                            val desc = rs.getString(TABLE_ELEMENT[7])
                            val scale = rs.getDouble(TABLE_ELEMENT[8])

                            getMatrixByID(id_pro, id_mat){matrix ->
                                //
                                searchElement = Element(col_ele, row_el, name, desc, scale,
                                    matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
                                    matrix.rowMax, matrix.columnMax, matrix.user, matrix.project,
                                    matrix.type)
                                elements.add(searchElement)
                                Log.d("DB", searchElement.toString())
                            }
                        }
                        callback(elements)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            callback(mutableListOf())
        }
    }

    fun getColumnElementsOnMatrix(matrix: Matrix, project: Project, callback: (List<Element>) -> Unit){
        //
        val sql = "SELECT * FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[5]} != 0 AND ${TABLE_ELEMENT[4]} = 0"
        Log.d("DB", sql)
        val elements = mutableListOf<Element>()
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, matrix.idMatrix)
                    statement.setLong(2, project.idProject)
                    statement.executeQuery().use { rs ->
                        var searchElement: Element
                        while (rs.next()) {
                            val id_mat = rs.getLong(TABLE_ELEMENT[1])
                            val id_pro = rs.getLong(TABLE_ELEMENT[2])
                            val us_us = rs.getString(TABLE_ELEMENT[3])
                            val row_el = rs.getInt(TABLE_ELEMENT[4])
                            val col_ele = rs.getInt(TABLE_ELEMENT[5])
                            val name = rs.getString(TABLE_ELEMENT[6])
                            val desc = rs.getString(TABLE_ELEMENT[7])
                            val scale = rs.getDouble(TABLE_ELEMENT[8])

                            getMatrixByID(id_pro, id_mat){matrix ->
                                //
                                searchElement = Element(col_ele, row_el, name, desc, scale,
                                    matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
                                    matrix.rowMax, matrix.columnMax, matrix.user, matrix.project,
                                    matrix.type)
                                elements.add(searchElement)
                                Log.d("DB", searchElement.toString())
                            }
                        }
                        callback(elements)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            callback(mutableListOf())
        }
    }

    fun getAssessElementsOnMatrix(matrix: Matrix, project: Project, user: User, column: Int, callback: (Int) -> Unit){
        //
        val sql = "SELECT COUNT(*) FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ? AND ${TABLE_ELEMENT[2]} = ? AND ${TABLE_ELEMENT[3]} = ? AND ${TABLE_ELEMENT[5]} = ? AND ${TABLE_ELEMENT[4]} > 0"
        Log.d("DB", sql)
        val sql2 = "SELECT COUNT(*) FROM ${TABLE_ELEMENT[0]} " +
                "WHERE ${TABLE_ELEMENT[1]} = ${matrix.idMatrix} AND ${TABLE_ELEMENT[2]} = ${project.idProject} AND ${TABLE_ELEMENT[3]} = ${user.user} AND ${TABLE_ELEMENT[5]} = $column AND ${TABLE_ELEMENT[4]} > 0"
        Log.d("DB", sql2)
        var searchElement: Int = 0
        try {
            getConnection().use { conn ->
                conn.prepareStatement(sql).use { statement ->
                    statement.setLong(1, matrix.idMatrix)
                    statement.setLong(2, project.idProject)
                    statement.setString(3, user.user)
                    statement.setInt(4, column)
                    statement.executeQuery().use { rs ->
                        while (rs.next()) {
                            val num = rs.getInt("")
                            searchElement = num
                            Log.d("DB", searchElement.toString())
                        }
                        callback(searchElement)
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            callback(0)
        }
    }


    fun insertData(connection: Connection, data: String) {
        //
        val sql = "INSERT INTO table_name(column_name) VALUES(?)"
        val statement: PreparedStatement = connection.prepareStatement(sql)
        statement.setString(1, data)
        statement.executeUpdate()
        statement.close()
    }

}
* */