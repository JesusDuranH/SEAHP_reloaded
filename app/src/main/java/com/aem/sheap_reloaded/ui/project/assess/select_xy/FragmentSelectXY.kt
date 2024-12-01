package com.aem.sheap_reloaded.ui.project.assess.select_xy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.Result
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.FragmentAssessSelectXyBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSelectXY: Fragment() {
    //
    private lateinit var user: User
    private lateinit var matrix: Matrix
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var criteriaList: MutableList<Criteria>
    private lateinit var alternativeList: MutableList<Alternative>
    private lateinit var allElementsByUser: MutableList<Element>

    private var _binding : FragmentAssessSelectXyBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterLeft: LeftAdapter
    private lateinit var adapterRight: RightAdapter
    private var positionLeft: Criteria = Criteria()
    private var positionRight: Criteria = Criteria()
    private var progress: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[FragmentSelectXYViewModel::class.java]

        _binding = FragmentAssessSelectXyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        setVar()
        if (!getData()) return root
        set()
        buttonOperation()

        return root
    }

    private fun setVar(){
        //
        val context = requireContext()

        user = User().get(context)
        config = Cipher()
        matrix = Matrix().get(context)
        project = Project().get(context)
    }

    private fun getData(): Boolean{
        //
        if (user == User()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            disable()
            return false
        }
        if (project == Project()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_project))
            disable()
            return false
        }
        if (matrix == Matrix()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_matrix))
            disable()
            return false
        }
        return true
    }

    private fun disable(){
        //
        with(binding){
            selectAssessButtonMaths.isEnabled = false
            selectAssessButtonSave.isEnabled = false
        }
    }

    private fun set(){
        //
        criteriaList = emptyList<Criteria>().toMutableList()
        Log.d("seahp_SelectXFragment", "set criteriaList initial empty: $criteriaList")
        alternativeList = emptyList<Alternative>().toMutableList()
        Log.d("seahp_SelectXFragment", "set alternativeList initial empty: $alternativeList")
        allElementsByUser = emptyList<Element>().toMutableList()
        Log.d("seahp_SelectXFragment", "set AllElementsByUser initial empty: $allElementsByUser")

        binding.textProject.text = matrix.nameMatrix

        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            allElementsByUser = Element().getAllElementOnMatrixByUser(matrix, project, user).toMutableList()
            when(matrix.idMatrix){
                1L -> {
                    Log.d("seahp_SelectXFragment", "set 1 criteriaList:")
                    criteriaList = Criteria().listByProject(project).toMutableList()
                    alternativeList = Alternative().listByProject(project).toMutableList()
                }
                2L -> {
                    Log.d("seahp_SelectXFragment", "set 2 criteriaList:")
                    criteriaList = Criteria().listByProject(project).toMutableList()

                    //Se agrega 1 deitem que usa para detectar que se creo la matriz
                    val total = criteriaList.size * criteriaList.size + 1
                    Log.d("seahp_SelectXFragment", "set 2 total: $total")

                    progress = (allElementsByUser.size.toDouble() / total.toDouble()) * 100
                    val getProgress = Result().byUsersNID(0, Participant(user, project))
                    Log.d("seahp_SelectXFragment", "set 2 getProgress: $getProgress")

                    if (getProgress == Result()){
                        Result().create(0, "Progess Matrix 2 ${project.nameProject}", Participant(user, project), progress)
                    } else if (getProgress.result != progress) {
                        Result().update(0, "Progess Matrix 2 ${project.nameProject}", Participant(user, project), progress)
                    }

                    Log.d("seahp_SelectXFragment", "set 2 progress: $progress")

                    checkValuesCriteriaCriteria()
                }
                else -> {
                    Log.d("seahp_SelectXFragment", "set 3 alternativeList:")
                    alternativeList = Alternative().listByProject(project).toMutableList()
                }
            }

            withContext(Dispatchers.Main){
                //
                loadingDialog.dismiss()
                binding.progressIndicator.progress = progress.toInt()
                recyclerViewLeft(criteriaList, alternativeList)
            }
        }
    }

    private fun checkValuesCriteriaCriteria(){
        //
        val newItems = emptyList<Criteria>().toMutableList()
        Log.d("seahp_SelectXFragment", "checkValuesCriteriaCriteria criteriaList: $criteriaList")
        for (item in criteriaList){
            val check = allElementsByUser.find { it.xElement == item.idCriteria && it.yElement == item.idCriteria }
            if (check == null) newItems.add(item)
        }
        Log.d("seahp_SelectXFragment", "checkValuesCriteriaCriteria newItems: $newItems")
        if (newItems.isNotEmpty()){
            val mat = Matrix(matrix.idMatrix, matrix.nameMatrix, matrix.descriptionMatrix,
                criteriaList.size, criteriaList.size, user, project, 2)
            Matrix().setDefaultScaleCriteriaCriteria(mat, newItems, user)
            allElementsByUser = Element().getAllElementOnMatrixByUser(matrix, project, user).toMutableList()
        }
    }

    private fun recyclerViewLeft(criteriaList: List<Criteria>, alternativeList: List<Alternative>){
        binding.rvLeft.layoutManager = LinearLayoutManager(requireContext())
        adapterLeft = LeftAdapter(criteriaList, alternativeList, allElementsByUser, matrix.idMatrix){ position ->
            positionLeft = position
            recyclerViewRight(criteriaList, alternativeList, allElementsByUser)
            Log.d("WeaChange", "$positionLeft - L")
        }
        binding.rvLeft.adapter = adapterLeft
    }

    private fun recyclerViewRight(criteriaList: List<Criteria>, alternativeList: List<Alternative>,
                                  listOfValues: List<Element>){
        binding.rvRight.layoutManager = LinearLayoutManager(requireContext())
        adapterRight = RightAdapter(criteriaList, alternativeList, listOfValues, positionLeft){ position ->
            positionRight = position
            Log.d("WeaChange", "$positionRight - R")
        }
        binding.rvRight.adapter = adapterRight
    }

    private fun buttonOperation(){
        //
        with(binding){
            //
            selectAssessButtonSave.setOnClickListener {
                findNavController().navigate(R.id.action_nav_project_select_xy_to_assess_perform)
            }
            if (matrix.idMatrix.toInt() == 1) selectAssessButtonMaths.isEnabled = false
            selectAssessButtonMaths.setOnClickListener {
                findNavController().navigate(R.id.action_nav_project_select_xy_to_result)
            }

            buttonChoice.setOnClickListener(){
                //Toast.makeText(requireContext() , "(${positionLeft.nameCriteria}, ${positionRight.nameCriteria})", Toast.LENGTH_SHORT).show()
                if (positionLeft == Criteria() || positionRight == Criteria())
                    Toast.makeText(requireContext() , "Selecciona ambos Criterios", Toast.LENGTH_SHORT).show()
                else{
                    Criteria().setX(positionLeft, requireContext())
                    Criteria().setY(positionRight, requireContext())
                    findNavController().navigate(R.id.action_nav_project_select_xy_to_assess_thing_thing)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}