package com.aem.sheap_reloaded.code.objects

import android.content.Context
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.things.AzureHelper
import com.aem.sheap_reloaded.code.things.Save
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
    private val save = Save()
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
        val threadCreateElement = Thread {
            AzureHelper().insertElement(newElement)
        }.apply {
            start()
            join()
        }
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

    fun toEvaluate(matrix: Matrix, project: Project, user: User, xElement: Long, yElement: Long): Element{
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

    fun getAllElementOnMatrixByUser(
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

    fun getAllElementOnMatrix(
        matrix: Matrix,
        project: Project
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListAllElements = Thread{
            AzureHelper().getAllElementsOnMatrix(matrix, project){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun getAlternativeRow(
        matrix: Matrix,
        project: Project,
        user: User,
        criteria: Criteria
    ): List<Element>{
        //
        var dataList = mutableListOf<Element>()
        val threadListElements = Thread{
            AzureHelper().getAlternativeRow(matrix, project, user, criteria){list ->
                dataList = list.toMutableList()
            }
        }.apply {
            start()
            join()
        }
        return dataList
    }

    fun setX(setElement: Element, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_ElementX),
            setElement.toByteArray(setElement),
            context.getString(R.string.alias_elementX))
    }

    fun getX(context: Context): Element{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_ElementX),
            context.getString(R.string.alias_elementX))
        return if (element != null) Element().toElement(element)
        else Element()
    }

    fun setY(setElement: Element, context: Context){
        save.saveOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_ElementY),
            setElement.toByteArray(setElement),
            context.getString(R.string.alias_elementY))
    }

    fun getY(context: Context): Element{
        val element = save.readOnFile(context,
            context.getString(R.string.save_folder),
            context.getString(R.string.save_ElementY),
            context.getString(R.string.alias_elementY))
        return if (element != null) Element().toElement(element)
        else Element()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Element) return false

        if (xElement != other.xElement) return false
        if (yElement != other.yElement) return false
        if (nameElement != other.nameElement) return false
        if (descriptionElement != other.descriptionElement) return false
        if (scaleElement != other.scaleElement) return false
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
        var result = nameElement.hashCode()
        result = 29 * result + descriptionElement.hashCode()
        result = 29 * result + scaleElement.hashCode()
        result = 29 * result + xElement.hashCode()
        result = 29 * result + yElement.hashCode()
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