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

open class Matrix(val idMatrix: Long,
                  val nameMatrix: String,
                  val descriptionMatrix: String? = null,
                  val rowMax: Int,
                  val columnMax: Int,
                  user: User,
                  project: Project,
                  type: Int
) : Participant(user, project, type), Serializable {
    //
    private val save = Save()
    constructor(): this (0,"",null,0,0,
        User(),Project(),3)


    private fun create(user: User, project: Project, x: Int, y: Int,
                        name:String, desc:String, id: Long): Matrix{
        val newMatrix = Matrix(id, name, desc, y, x, user, project, 2)
        AzureHelper().insertMatrix(newMatrix)
        return newMatrix
    }

    fun listByProjectM(project: Project): List<Matrix> {
        //
        var dataList = mutableListOf<Matrix>()
        val threadListMatrix = Thread {
            AzureHelper().getMatrixListByProject(project){ list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun criteriaAlternative(
        user: User,
        project: Project
    ): Matrix {
        //
        var matrix = Matrix()
        val x = Criteria().listByProject(project).size
        val y = Alternative().listByProject(project).size
        val name = "Criteria - Alternative | Project: ${project.nameProject}"
        val desc = "Criteria - Alternative Evaluation Matrix with $x Criteria " +
                "and $y Alternatives"
        val threadMatrix = Thread {
            matrix = create(user, project, x, y, name, desc,1)
        }.apply {
            start()
            join()
        }
        return matrix
    }

    fun criteriaCriteria(
        user: User,
        project: Project
    ): Matrix {
        //
        var matrix = Matrix()
        val x = Criteria().listByProject(project).size
        val name = "Criteria - Criteria | Project: ${project.nameProject}"
        val desc = "Criteria - Criteria Evaluation Matrix with $x Criteria"
        val threadMatrix = Thread {
            //
            matrix = create(user, project, x, x, name, desc,2)
        }.apply {
            start()
            join()
        }
        return matrix
    }

    fun alternativeAlternative(
        user: User,
        project: Project
    ): List<Matrix> {
        //
        val listCriteria = Criteria().listByProject(project)
        val y = Alternative().listByProject(project).size

        val list = mutableListOf<Matrix>()
        var count: Long = 3
        for (item in listCriteria) {
            val name = "Alternative - Alternative by ${item.nameCriteria} | Project: ${project.nameProject}"
            val desc = "Alternative - Alternative Evaluation Matrix by ${item.nameCriteria} " +
                    "with $y Rows and Columns"
            var matrix = Matrix()
            val threadMatrix = Thread {
                matrix = create(user, project, y, y, name, desc, count)
            }.apply {
                start()
                join()
            }
            list.add(matrix)
            count++
        }
        return list
    }

    fun setDefaultScaleCriteriaCriteria
                (matrix: Matrix, rowElement: List<Criteria>, user: User){
        //
        var name: String
        val scale = 1.0
        val desc = "Matriz : ${matrix.nameMatrix} | Proyecto: ${matrix.project.nameProject}"

        for (item in rowElement){
            name = "${item.nameCriteria} - ${item.nameCriteria}"
            val threadElement = Thread {
                //
                Element().create(item.idCriteria,item.idCriteria,
                    name, desc, scale, matrix, matrix.project, user)
            }.apply {
                start()
                join()
            }
        }
    }

    fun set(setMatrix: Matrix, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_matrix),
            Matrix().toByteArray(setMatrix),
            context.getString(R.string.alias_matrix))
    }

    fun get(context: Context): Matrix{
        val matrix = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_matrix),
            context.getString(R.string.alias_matrix))
        return if (matrix != null) Matrix().toMatrix(matrix)
        else Matrix()
    }

    fun toByteArray(matrix: Matrix): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(matrix)
        return byteArrayOutputStream.toByteArray()
    }

    fun toMatrix(byteArray: ByteArray): Matrix {
        return ObjectInputStream(ByteArrayInputStream(byteArray)).readObject() as Matrix
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix) return false

        if (idMatrix != other.idMatrix) return false
        if (nameMatrix != other.nameMatrix) return false
        if (descriptionMatrix != other.descriptionMatrix) return false
        if (rowMax != other.rowMax) return false
        if (columnMax != other.columnMax) return false
        if (user != other.user) return false
        if (project != other.project) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idMatrix.hashCode()
        result = 29 * result + idMatrix.hashCode()
        result = 29 * result + nameMatrix.hashCode()
        result = 29 * result + descriptionMatrix.hashCode()
        result = 29 * result + rowMax.hashCode()
        result = 29 * result + columnMax.hashCode()
        result = 29 * result + user.hashCode()
        result = 29 * result + project.hashCode()
        result = 29 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "\nMatrix($idMatrix, \"$nameMatrix\", \"$descriptionMatrix\", $rowMax, $columnMax, " +
                "${project.idProject}, \"${project.nameProject}\", \"${project.descriptionProject}\")"
    }
/*




    fun criteriaLineOnline(list: List<Criteria>, matrix: Matrix, row: Boolean, user: User): MutableList<Element> {
        //
        var z = 0
        val listOfElements = mutableListOf<Element>()
        val threadElement = Thread {
            //
            for (item in list){
                if (item.idProject == matrix.project.idProject){
                    z++
                    if (row)   listOfElements.add(Element().createElementOnline(z, 0, item.nameCriteria,
                        item.descriptionCriteria, null, matrix, matrix.project, user))
                    else        listOfElements.add(Element().createElementOnline(0, z, item.nameCriteria,
                        item.descriptionCriteria, null, matrix, matrix.project, user))
                }
            }
        }.apply {
            start()
            join()
        }
        return listOfElements
    }

    fun alternativeLineOnline(list: List<Alternative>, matrix: Matrix, row: Boolean, user: User): MutableList<Element> {
        //
        var z = 0
        val listOfElements = mutableListOf<Element>()
        val threadElement = Thread {
            //
            for (item in list){
                z++
                if (row)   listOfElements.add(Element().createElementOnline(z, 0, item.nameAlternative,
                    item.descriptionAlternative, null, matrix, matrix.project, user))
                else        listOfElements.add(Element().createElementOnline(0, z, item.nameAlternative,
                    item.descriptionAlternative, null, matrix, matrix.project, user))
                Log.d("Line", "$listOfElements")
            }
        }.apply {
            start()
            join()
        }
        return listOfElements
    }

    fun getRowsOnline(matrix: Matrix, project: Project): List<Element>{
        //
        //val allElements = Element().getElementOAllMatrixOnline(matrix, project)
        val listRowElement = Element().getElementRowMatrixOnline(matrix, project)
        /*for (item in allElements){
            if (item.xElement == 0 && item.yElement > 0){
                listRowElement.add(item)
            }
        }*/
        return listRowElement
    }

    fun getItemAssessOnline(matrix: Matrix, project: Project, user: User, column: Int): Int{
        //
        val sum = Element().getElementAssessOnMatrixOnline(matrix, project, user, column)
        return sum
    }

    fun getColumnsOnline(matrix: Matrix, project: Project): List<Element>{
        //
        //val allElements = Element().getElementOAllMatrixOnline(matrix, project)
        val listColumnElement = Element().getElementColumnMatrixOnline(matrix, project)
        /*for (item in allElements){
            if (item.yElement == 0 && item.xElement > 0){
                listColumnElement.add(item)
            }
        }*/
        return listColumnElement
    }

    */
}