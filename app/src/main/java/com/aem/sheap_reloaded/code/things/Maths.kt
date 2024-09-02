package com.aem.sheap_reloaded.code.things

import android.util.Log
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.ui.project.assess.result.alone.Result
import com.github.mikephil.charting.data.PieEntry
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.pow

class Maths {
    //
    val riTable = mutableListOf(0.0,
        0.0,
        0.0,
        0.525,
        0.882,
        1.115,
        1.252,
        1.341,
        1.404,
        1.452,
        1.484,
        1.513,
        1.535,
        1.555,
        1.570,
        1.583,
        1.595
    )

    var text = ""

    val items = mutableListOf<PieEntry>()

    fun consistencyRatio(allElements: List<Element>): Result {
        //
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP

        val n = allElements[0].rowMax
        val nMax = matrixWithAverageVector(allElements)

        val ic = (nMax - n) / (n - 1)
        Log.d("seahp_Maths", "consistencyRatio: IC = $ic")
        //text += "Indice de Consistencia = $ic\n"

        val ir = (1.98 * (n - 2)) / n
        Log.d("seahp_Maths", "consistencyRatio: IR = $ir")
        //text += "Indice de Consistencia Aleatorio = $ir\n"

        val cr = ic / ir
        var percent = (cr * 100)
        var r = df.format(percent)
        Log.d("seahp_Maths", "consistencyRatio: CR = $cr")
        //text += "Razon de consistencia = $r%\n"
        text += "Inconsistencia: $r%\n"

        val irT = riTable[n]
        Log.d("seahp_Maths", "consistencyRatio: IR Table = $irT")
        //text += "Indice de Consistencia Aleatorio por Tabla = $irT\n"

        val crT = ic / irT
        percent = (crT * 100)
        r = df.format(percent)
        Log.d("seahp_Maths", "consistencyRatio: CR Table = $crT")
        //text += "Razon de consistencia por Tabla = $r%\n"
        return Result(text, items)
    }

    fun matrixWithAverageVector(allElements: List<Element>): Double{
        //
        val criteriaList = Criteria().listByProject(allElements[0].project)
        val averageVector = averageVector(allElements, criteriaList)
        val size = allElements[0].rowMax - 1
        val elementAV = mutableListOf<Double>()
        for (y in 0..size){
            //
            var sumElements = 0.0
            for (x in 0..size){
                //
                val element = allElements.find { it.xElement == criteriaList[x].idCriteria && it.yElement == criteriaList[y].idCriteria } ?: Element()
                val scale = element.scaleElement ?: 0.0
                sumElements += scale * averageVector[x]
            }
            elementAV.add(sumElements)
            Log.d("seahp_Maths", "matrixWithAverageVector: Element * AV $y = $sumElements")
            //text += "Element * AV $y = $sumElements\n"
        }
        Log.d("seahp_Maths", "matrixWithAverageVector: Element * AV total ${elementAV.sum()}")
        //text += "Element * AV total ${elementAV.sum()}\n"
        return elementAV.sum()
    }

    fun averageVector(allElements: List<Element>, criteriaList: List<Criteria>): List<Double>{
        //
        val size = allElements[0].rowMax - 1
        val sumColumns: List<Double> = sumAllColumns(allElements, criteriaList)
        var normalized: MutableList<Double>
        val vectorList = mutableListOf<Double>()
        for (y in 0..size){
            normalized = emptyList<Double>().toMutableList()
            for (x in 0..size){
                val element = allElements.find { it.xElement== criteriaList[x].idCriteria && it.yElement == criteriaList[y].idCriteria } ?: Element()
                val scale = element.scaleElement ?: 0.0
                val normalizeValue = scale / sumColumns[x]
                //Log.d("Maths", "($x, $y) $scale / ${sumColumns[x]} = $normalizeValue")
                normalized.add(normalizeValue)
            }
            Log.d("seahp_Maths", "averageVector row: $y = $normalized")
            //text += "row $y = $normalized\n"
            val vector = normalized.average()
            vectorList.add(vector)
            Log.d("seahp_Maths", "averageVector vector: $y = $vector")
            val percent = (vector * 100)
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.HALF_UP
            //text += "vector $y = $vector\n"
            val element = criteriaList[y].nameCriteria
            //text += "Preferencia de ${element.nameElement} = ${df.format(percent)}%\n"
            items.add(PieEntry(df.format(percent).toFloat(), element))
        }
        Log.d("seahp_Maths", "averageVector: Vectors = $vectorList")
        //text += "Vector = $vectorList\n"
        Log.d("seahp_Maths", "averageVector: Vector Sum = ${vectorList.sum()}")
        //text += "Vector Sum = ${vectorList.sum()}\n"
        return vectorList
    }

    fun sumAllColumns(allElements: List<Element>, criteriaList: List<Criteria>): List<Double>{
        //
        val listOfResult = mutableListOf<Double>()
        val matrixSize = allElements[0].rowMax - 1
        for (column in 0..matrixSize){
            //
            val result = sumColumn(allElements, criteriaList[column])
            Log.d("seahp_Maths", "sumAllColumns: ${criteriaList[column].nameCriteria} $column = $result")
            //text += "column $column = $result \n"
            listOfResult.add(result)
        }
        Log.d("seahp_Maths", "sumAllColumns listOfResult: $listOfResult")
        //text += "$listOfResult\n"
        return listOfResult
    }

    fun sumColumn(allElements: List<Element>, column: Criteria): Double{
        //
        var result = 0.0
        for (item in allElements){
            if (item.xElement == column.idCriteria && item.scaleElement != null)
            {
                //Log.d("seahp_Maths", "sumColumn ${item.nameElement} ${column.nameCriteria}: ${item.scaleElement}")
                result += item.scaleElement
            }
        }
        return result
    }

    fun mediaGeometrica(allElements: List<Element>, participant: List<Participant>,
                        criteriaList: List<Criteria>): Result {
        //
        val nMax = allElements[0].rowMax - 1
        val nParticipant = participant.size
        val geometricData = mutableListOf<Element>()

        for (x in criteriaList){
            for (y in criteriaList){
                val assess = mutableListOf<Double>()
                for (p in participant){
                    val search = allElements.find { it.xElement == x.idCriteria && it.yElement == y.idCriteria && it.user.user == p.user.user }
                    if (search?.scaleElement != null && search.scaleElement != 0.0){
                        //
                        assess.add(search.scaleElement)
                    }
                }
                Log.d("seahp_Maths", "mediaGeometrica assess: $assess")
                if (assess.size != 0){
                    var mul: Double = 1.0
                    for (num in assess){
                        mul *= num
                        Log.d("seahp_Maths", "mediaGeometrica mul($mul)")
                    }
                    val raiz: Double = (1.0/assess.size)
                    Log.d("seahp_Maths", "mediaGeometrica raiz($raiz)")
                    val result = mul.pow(raiz)
                    Log.d("seahp_Maths", "mediaGeometrica result($result)")
                    val nameItem = allElements.find { it.yElement == y.idCriteria }!!.nameElement
                    val geometricElement = Element(x.idCriteria, y.idCriteria, /*"Geometric Result ($x,$y) = $nameItem"*/nameItem, null, result,
                        allElements[0].idMatrix, allElements[0].nameMatrix, allElements[0].descriptionMatrix,
                        allElements[0].rowMax, allElements[0].columnMax, allElements[0].user,
                        allElements[0].project, allElements[0].type)
                    geometricData.add(geometricElement)
                }
            }
        }
        Log.d("seahp_Maths", "mediaGeometrica geometricData: $geometricData")
        return consistencyRatio(geometricData)
    }
}