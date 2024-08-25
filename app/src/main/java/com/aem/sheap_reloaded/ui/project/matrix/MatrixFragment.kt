package com.aem.sheap_reloaded.ui.project.matrix

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.FragmentAssessMatrixBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatrixFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var matrixList: MutableList<Matrix>
    private lateinit var configProject: SEAHP

    private var _binding: FragmentAssessMatrixBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val assessPerformViewModel =
            ViewModelProvider(this)[MatrixViewModel::class.java]

        _binding = FragmentAssessMatrixBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAssessPerform
        assessPerformViewModel.text.observe(viewLifecycleOwner){
            textView.text = it
        }

        setVar()
        if (!getData()) return root
        set()

        return root
    }

    private fun setVar(){
        //
        val context = requireContext()
        user = User().get(context)
        config = Cipher()
        project = Project().get(context)
        configProject = SEAHP()
    }

    private fun getData(): Boolean{
        //
        if (user == User()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            binding.assessButtonResult.isEnabled = false
            return false
        }
        if (project == Project()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_project))
            binding.assessButtonResult.isEnabled = false
            return false
        }
        return true
    }

    private fun set(){
        //
        matrixList = emptyList<Matrix>().toMutableList()
        Log.d("seahp_MatrixFragment", "set matrixList empty: $matrixList")

        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {

            matrixList = Matrix().listByProjectM(project).toMutableList()
            Log.d("seahp_MatrixFragment", "set seatch matrixList: $matrixList")
            withContext(Dispatchers.Main){
                setRecyclerView(matrixList)
            }

            /*val i = setMatrix()
            if (i) {
                val mList2 = Matrix().listOfMatrixByProjectOnline(project)
                withContext(Dispatchers.Main){
                    matrixList = mList2.toMutableList()
                    setRecyclerView(matrixList)
                }
            }
            Log.d("Etapa", "Segunda etapa = $matrixList \n SetMatrix = $i")*/

            withContext(Dispatchers.Main){
                loadingDialog.dismiss()
            }
        }

        Log.d("Matrix", "matrixList = $matrixList")
    }

    private fun setRecyclerView(dataList: List<Matrix>){
        //
        binding.assessMatrixRecyclerViewName.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MatrixRecyclerViewAdapter(dataList)
        binding.assessMatrixRecyclerViewName.adapter = adapter
    }
}

/*
class AssessPerformFragment: Fragment() {

    private fun setMatrix(): Boolean{
        //
        Log.d("Matrix", "matrixList = $matrixList")
        var isChange = false

        val mat = matrixList.find { it.idMatrix.toInt() == 1 }
        val mat_1 = matrixList.find { it.idMatrix.toInt() == 1 }
        val mat_2 = matrixList.find { it.idMatrix.toInt() == 2 }
        val mat_3 = matrixList.find { it.idMatrix.toInt() == 3 }

        val matrix: Matrix = if (isOnline){
            //
            if (mat_3 == null){
                if (mat_2 == null){
                    if (mat_1 == null){
                        createMatrixOnline(1)
                        //createMatrixOnline(2)
                        //createMatrixOnline(3)
                        Log.d("Matrix", "Crate Matrix 1 with Data")
                        isChange = true
                    } else {
                        //
                        val element = Element().getElementOfCreateMatrixOnline(mat_1, project, user)
                        val userValues = element.find { it.yElement == 0 && it.xElement == 0 }
                        if (userValues == null){
                            //
                            for (item in matrixList) createValuesOnline(item)
                            Log.d("Matrix", "Crate only Data 1")
                            isChange = true
                        } else Log.d("Matrix", "Don't create nothing 1")

                    }
                    //createMatrixOnline(1)
                    createMatrixOnline(2)
                    //createMatrixOnline(3)
                    Log.d("Matrix", "Crate Matrix 2 with Data")
                    isChange = true
                } else {
                    //
                    val element = Element().getElementOfCreateMatrixOnline(mat_2, project, user)
                    val userValues = element.find { it.yElement == 0 && it.xElement == 0 }
                    if (userValues == null){
                        //
                        for (item in matrixList) createValuesOnline(item)
                        Log.d("Matrix", "Crate only Data 2")
                        isChange = true
                    } else Log.d("Matrix", "Don't create nothing 2")
                }
                //createMatrixOnline(1)
                //createMatrixOnline(2)
                createMatrixOnline(3)
                Log.d("Matrix", "Crate Matrix 3 with Data")
                isChange = true
            } else {
                //
                val element = Element().getElementOfCreateMatrixOnline(mat_3, project, user)
                val userValues = element.find { it.yElement == 0 && it.xElement == 0 }
                if (userValues == null){
                    //
                    for (item in matrixList) createValuesOnline(item)
                    Log.d("Matrix", "Crate only Data 3")
                    isChange = true
                } else Log.d("Matrix", "Don't create nothing 3")
            }

            Matrix()
        } else {
            //
            if (mat == null){
                createMatrixOffline(1)
                createMatrixOffline(2)
                createMatrixOffline(3)
                Log.d("Matrix", "Crate Matrix with Data")
                isChange = true
            } else {
                val element = Element().getElementOAllMatrixOffline(mat, project, dbHelper)
                val userValues = element.find { it.yElement == 0 && it.xElement == 0 && it.user == user }
                if (userValues == null){
                    //
                    for (item in matrixList) createValuesOffline(item)
                    Log.d("Matrix", "Crate only Data")
                    isChange = true
                } else Log.d("Matrix", "Don't create nothing")

            }
            Matrix()
        }
        //dataList.add(matrixCA)
        return isChange
    }

    private fun createValuesOnline(matrix: Matrix): Matrix{
        //
        val mat = Matrix(matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
            matrix.rowMax, matrix.columnMax, user, project, matrix.type)
        val matII = when (mat.idMatrix.toInt()){
            1 -> {
                val threadElement = Thread {
                    //
                    val element = Element().createElementOnline(0,0,"Table Create",null,
                        0.0,mat, mat.project, mat.user)
                }.apply {
                    start()
                    join()
                }
                mat
            }
            else ->{
                //
                val row = Matrix().getRowsOnline(mat, project)
                Matrix().setDefaultScaleOnline(mat, row, row, user)

                val threadElement = Thread {
                    //
                    val element = Element().createElementOnline(0,0,"Table Create",null,
                        0.0,mat, mat.project, mat.user)
                }.apply {
                    start()
                    join()
                }
                mat
            }
        }
        return mat
    }

    private fun createValuesOffline(matrix: Matrix): Matrix{
        //
        val mat = Matrix(matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
                        matrix.rowMax, matrix.columnMax, user, project, matrix.type)
        val matII = when (mat.idMatrix.toInt()){
            1 -> {
                val element = Element().createElementOffline(0,0,"Table Create",null,
                    0.0,mat, mat.project, mat.user, dbHelper)
                mat
            }
            else ->{
                //
                val row = Matrix().getRowsOffline(mat, project, dbHelper)
                Matrix().setDefaultScaleOffline(mat, row, row, user, dbHelper)
                val element = Element().createElementOffline(0,0,"Table Create",null,
                    0.0,mat, mat.project, mat.user, dbHelper)
                mat
            }
        }
        return mat
    }

    private fun createMatrixOnline(op: Int): Matrix{

        var matrix = Matrix()
        matrix = when (op) {
            1 -> { //Criteria - Alternative
                val mat = Matrix().matrixCriteriaAlternativeOnline(user, project)
                val alternativeList = Alternative().listOfAlternativeByProjectOnline(project)
                val criteriaList = Criteria().listOfCriteriaByProjectOnline(project)
                val row = Matrix().criteriaLineOnline(criteriaList, mat, true, user)
                val column = Matrix().alternativeLineOnline(alternativeList, mat, false, user)
                val threadElement = Thread {
                    //
                    val element = Element().createElementOnline(0,0,"Table Create",null,
                        0.0,mat, mat.project, mat.user)
                }.apply {
                    start()
                    join()
                }
                mat
            }
            2 -> { //Criteria - Criteria
                val mat = Matrix().matrixCriteriaCriteriaOnline(user, project)
                val criteriaList = Criteria().listOfCriteriaByProjectOnline(project)
                val row = Matrix().criteriaLineOnline(criteriaList, mat, true, user)
                val column = Matrix().criteriaLineOnline(criteriaList, mat, false, user)
                Matrix().setDefaultScaleOnline(mat, row, column, user)
                val threadElement = Thread {
                    //
                    val element = Element().createElementOnline(0,0,"Table Create",null,
                        0.0,mat, mat.project, mat.user)
                }.apply {
                    start()
                    join()
                }
                mat
            }
            3 -> { //Alternative - Alternative
                val alternativeMatrixList = Matrix().matrixAlternativeAlternativeOnline(user, project)
                val alternativeList = Alternative().listOfAlternativeByProjectOnline(project)
                for (item in alternativeMatrixList){
                    val row = Matrix().alternativeLineOnline(alternativeList, item, true, user)
                    val column = Matrix().alternativeLineOnline(alternativeList, item, false, user)
                    Matrix().setDefaultScaleOnline(item, row, column, user)
                    val threadElement = Thread {
                        //
                        val element = Element().createElementOnline(0,0,"Table Create",
                            null, 0.0, item, item.project, item.user)
                    }.apply {
                        start()
                        join()
                    }
                }
                Matrix()
            }

            else -> Matrix()
        }
        return matrix
        }

    private fun createMatrixOffline(op: Int): Matrix{
        val matrix = when (op){
            1 -> { //Criteria - Alternative
                val mat = Matrix().matrixCriteriaAlternativeOffline(user, project, dbHelper)
                val alternativeList = Alternative().listOfAlternativeByProjectOffline(project, dbHelper)
                val criteriaList = Criteria().listOfCriteriaByProjectOffline(project, dbHelper)
                val row = Matrix().criteriaLineOffline(criteriaList, mat, true, user, dbHelper)
                val column = Matrix().alternativeLineOffline(alternativeList, mat, false, user, dbHelper)
                val element = Element().createElementOffline(0,0,"Table Create",null,
                                                        0.0,mat, mat.project, mat.user, dbHelper)
                mat
            }
            2 -> { //Criteria - Criteria
                val mat = Matrix().matrixCriteriaCriteriaOffline(user, project, dbHelper)
                val criteriaList = Criteria().listOfCriteriaByProjectOffline(project, dbHelper)
                val row = Matrix().criteriaLineOffline(criteriaList, mat, true, user, dbHelper)
                val column = Matrix().criteriaLineOffline(criteriaList, mat, false, user, dbHelper)
                Matrix().setDefaultScaleOffline(mat, row, column, user, dbHelper)
                val element = Element().createElementOffline(0,0,"Table Create",null,
                    0.0,mat, mat.project, mat.user, dbHelper)
                mat
            }
            3 -> { //Alternative - Alternative
                val alternativeMatrixList = Matrix().matrixAlternativeAlternativeOffline(user, project, dbHelper)
                val alternativeList = Alternative().listOfAlternativeByProjectOffline(project, dbHelper)
                for (item in alternativeMatrixList){
                    val row = Matrix().alternativeLineOffline(alternativeList, item, true, user, dbHelper)
                    val column = Matrix().alternativeLineOffline(alternativeList, item, false, user, dbHelper)
                    Matrix().setDefaultScaleOffline(item, row, column, user, dbHelper)
                    val element = Element().createElementOffline(0,0,"Table Create",
                        null, 0.0, item, item.project, item.user, dbHelper)
                }
                Matrix()
            }
            else -> Matrix()
        }
        return matrix
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
* */