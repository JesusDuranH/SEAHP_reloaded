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
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Participant
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
        Log.d("seahp_MatrixFragment", "set matrixList initial empty: $matrixList")

        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {

            matrixList = Matrix().listByProjectM(project).toMutableList()
            Log.d("seahp_MatrixFragment", "set search matrixList: $matrixList")
            withContext(Dispatchers.Main){
                setRecyclerView(matrixList)
            }

            val i = setMatrix()
            if (i) {
                matrixList = Matrix().listByProjectM(project).toMutableList()
                withContext(Dispatchers.Main){
                    setRecyclerView(matrixList)
                }
            }
            Log.d("Etapa", "Segunda etapa = $matrixList \n SetMatrix = $i")

            withContext(Dispatchers.Main){
                loadingDialog.dismiss()
            }
        }

        Log.d("Matrix", "matrixList = $matrixList")
    }

    private fun setMatrix(): Boolean{
        //
        var isChange = false
        val type = Participant().isAdminInThis(user, project).type

        val mat_1 = matrixList.find { it.idMatrix.toInt() == 1 }
        val mat_2 = matrixList.find { it.idMatrix.toInt() == 2 }
        //val mat_3 = matrixList.find { it.idMatrix.toInt() == 3 }

        if (type == 2){
            if (mat_1 == null){
                Log.d("seahp_MatrixFragment", "setMatrix: Create Matrix & Values 1")
                createValues(createMatrix(1))
                isChange = true
            }
            if (mat_2 == null){
                Log.d("seahp_MatrixFragment", "setMatrix: Create Matrix & Values 2")
                createValues(createMatrix(2))
                isChange = true
            }
        } else {
            if (mat_1 != null) {
                Log.d("seahp_MatrixFragment", "setMatrix: Create Values 1")
                val create = Element().toEvaluate(mat_1, project, user, Element(), Element())
                if (create.nameElement == "") createValues(mat_1)
            }
            if (mat_2 != null) {
                Log.d("seahp_MatrixFragment", "setMatrix: Create Values 2")
                val create = Element().toEvaluate(mat_2, project, user, Element(), Element())
                if (create.nameElement == "") createValues(mat_2)
            }
        }
        Log.d("seahp_MatrixFragment", "setMatrix isChange: $isChange")
        return isChange
    }

    private fun createMatrix(op: Int): Matrix{
        //
        val matrix = when (op) {
            1 -> { //Criteria - Alternative
                val mat = Matrix().criteriaAlternative(user, project)
                mat
            }
            2 -> { //Criteria - Criteria
                val mat = Matrix().criteriaCriteria(user, project)
                mat
            }
            // Programar Alternativa-Alternativa por criterio
            /*3 -> {
                //GG
            }*/

            else -> Matrix()
        }
        return matrix
    }

    private fun createValues(matrix: Matrix): Matrix{
        //
        val mat = Matrix(matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
            matrix.rowMax, matrix.columnMax, user, project, matrix.type)
        val matII = when (mat.idMatrix.toInt()){
            1 -> {
                val threadElement = Thread {
                    //
                    val element = Element().create(0,0,"Table Create",null,
                        0.0,mat, mat.project, mat.user)
                }.apply {
                    start()
                    join()
                }
                mat
            }
            2 ->{
                //
                val row = Criteria().listByProject(project)
                Matrix().setDefaultScaleCriteriaCriteria(mat, row, user)

                val threadElement = Thread {
                    //
                    val element = Element().create(0,0,"Table Create",null,
                        0.0,mat, mat.project, mat.user)
                }.apply {
                    start()
                    join()
                }
                mat
            }
            else -> Matrix()
        }
        return mat
    }

    private fun setRecyclerView(dataList: List<Matrix>){
        //
        binding.assessMatrixRecyclerViewName.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MatrixRecyclerViewAdapter(dataList)
        binding.assessMatrixRecyclerViewName.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}