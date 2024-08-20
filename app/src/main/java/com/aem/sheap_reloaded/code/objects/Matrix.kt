package com.aem.sheap_reloaded.code.objects

import com.aem.sheap_reloaded.code.things.AzureHelper
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
            AzureHelper().getMatrixByProject(project){ list ->
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
        val desc = "Criteria - Criteria Evaluation Matrix with $x Criteria and Alternatives"
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

    fun toByteArray(matrix: Matrix): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(matrix)
        return byteArrayOutputStream.toByteArray()
    }

    fun toMatrix(byteArray: ByteArray): Matrix {
        return ObjectInputStream(ByteArrayInputStream(byteArray)).readObject() as Matrix
    }

    override fun toString(): String {
        return "Matrix($idMatrix, \"$nameMatrix\", \"$descriptionMatrix\", $rowMax, $columnMax, " +
                "${project.idProject}, \"${project.nameProject}\", \"${project.descriptionProject}\"), \n"
    }
/*


    fun setDefaultScaleOnline(matrix: Matrix, rowElement: List<Element>, columnElement: List<Element>, user: User){
        //
        var name: String
        val scale = 1.0
        val desc = "Matriz : ${matrix.nameMatrix} | Proyecto: ${matrix.project.nameProject}"

        for (i in 0 until matrix.rowMax){
            name = "${rowElement[i].nameElement} - ${columnElement[i].nameElement}"
            val threadElement = Thread {
                //
                Element().createElementOnline(i+1,i+1,name, desc, scale, matrix, matrix.project, user)
            }.apply {
                start()
                join()
            }
        }
    }

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