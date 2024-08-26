package com.aem.sheap_reloaded.code.objects

import com.aem.sheap_reloaded.code.things.AzureHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class Element(val xElement: Long,
              val yElement: Long,
              val nameElement: String,
              val descriptionElement: String?,
              val scaleElement: Double?,
              idMatrix: Long,
              nameMatrix: String,
              descriptionMatrix: String? = null,
              rowMax: Int,
              columnMax: Int,
              user: User,
              project: Project,
              type: Int
) : Matrix(idMatrix,
    nameMatrix,
    descriptionMatrix,
    rowMax,
    columnMax,
    user,
    project,
    type), Serializable {
    //
    constructor(): this (0,0,"","", 0.0,
        0,"",null,0,0, User(), Project(), 0)

    constructor(scale: Double): this (0, 0, "", "", scale,
        0, "", null, 0, 0, User(), Project(), 0
    )

    fun create(x: Long, y:Long, name: String, desc: String?, scale: Double? = null,
                            matrix: Matrix, project: Project, user: User, type: Int = matrix.type): Element{
        //
        val newElement = Element(x, y, name, desc, scale,
            matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix, matrix.rowMax, matrix.columnMax,
            user, project, type)
        AzureHelper().insertElement(newElement)
        return newElement
    }

    fun updateScale(elementUpdate: Element, scale: Double, mirror: Boolean): Element{
        //
        val updateScale = with(elementUpdate){
            if (mirror){
                //
                Element(yElement, xElement, nameElement, descriptionElement, scale,
                    idMatrix, nameMatrix, descriptionMatrix, rowMax, columnMax, user,
                    project, type)
            } else {
                Element(xElement, yElement, nameElement, descriptionElement, scale,
                    idMatrix, nameMatrix, descriptionMatrix, rowMax, columnMax, user,
                    project, type)
            }
        }
        val threadElement = Thread {
            AzureHelper().updateElementByID(updateScale)
        }.apply {
            start()
            join()
        }
        return updateScale
    }

    fun toEvaluate(matrix: Matrix, project: Project, user: User, xElement: Element, yElement: Element): Element{
        //
        var evaluate = Element()
        val threadEvaluateElements = Thread{
            AzureHelper().getOneElementToEvaluateOnMatrix(matrix, project, user, xElement, yElement){element ->
                evaluate = element
            }
        }.apply {
            start()
            join()
        }
        return evaluate
    }

    fun getElementAllMatrixByUser(
        matrix: Matrix,
        project: Project,
        user: User
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getAllElementsOnMatrixByUser(matrix, project, user){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun toByteArray(element: Element): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(element)
        return byteArrayOutputStream.toByteArray()
    }

    fun toElement(byteArray: ByteArray): Element {
        return ObjectInputStream(ByteArrayInputStream(byteArray)).readObject() as Element
    }

    override fun toString(): String {
        return "\nElement($xElement, $yElement, \"$nameElement\", $scaleElement, " +
                "$idMatrix, $nameMatrix, ${user.user}, ${project.nameProject})"
    }
}

/*
class Element(){
    //
    fun getAllAssessElementOnMatrixOnline(
        matrix: Matrix,
        project: Project
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getAllAssessElementsOnMatrixAllUsers(matrix, project){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }



    fun getElementOfCreateMatrixOnline(
        matrix: Matrix, project: Project, user: User
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getOneElementOfCreateOnMatrix(matrix, project, user){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun getElementToEvaluateMatrixOnline(
        matrix: Matrix, project: Project, user: User,
        xElement: Element, yElement: Element
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getOneElementToEvaluateOnMatrix(matrix, project, user, xElement, yElement){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun getElementRowMatrixOnline(
        matrix: Matrix,
        project: Project
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getRowElementsOnMatrix(matrix, project){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun getElementRowEvaluationMatrixOnline(
        matrix: Matrix,
        project: Project,
        user: User,
        element: Element
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getRowElementsEvaluationOnMatrix(matrix, project, user, element){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun getElementColumnMatrixOnline(
        matrix: Matrix,
        project: Project
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getColumnElementsOnMatrix(matrix, project){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun getElementAssessOnMatrixOnline(
        matrix: Matrix,
        project: Project,
        user: User,
        column: Int
    ): Int{
        //
        var data: Int = 0
        val threadListElements = Thread{
            AzureHelper().getAssessElementsOnMatrix(matrix, project, user, column){list ->
                data = list
            }
        }.apply {
            start()
            join()
        }
        return data
    }


}

* */