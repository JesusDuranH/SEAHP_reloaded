package com.aem.sheap_reloaded.ui.project.assess.select_y

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Element
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.databinding.FragmentAssessSelectYBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectYFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var matrix: Matrix
    private lateinit var project: Project
    private lateinit var criteriaList: MutableList<Criteria>
    private lateinit var alternativeList: MutableList<Alternative>

    private var _binding: FragmentAssessSelectYBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val selectAssessViewModel =
            ViewModelProvider(this)[SelectYViewModel::class.java]

        _binding = FragmentAssessSelectYBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAssessSelect
        selectAssessViewModel.text.observe(viewLifecycleOwner){
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
        matrix = Matrix().get(context)
        project = Project().get(context)
    }

    private fun getData(): Boolean{
        //
        if (user == User()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            return false
        }
        if (project == Project()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_project))
            return false
        }
        if (matrix == Matrix()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_matrix))
            return false
        }
        return true
    }

    private fun set(){
        //
        criteriaList = emptyList<Criteria>().toMutableList()
        Log.d("seahp_SelectYFragment", "set criteriaList initial empty: $criteriaList")
        alternativeList = emptyList<Alternative>().toMutableList()
        Log.d("seahp_SelectYFragment", "set alternativeList initial empty: $alternativeList")

        var criteriaX: Criteria
        var alternativeX: Alternative
        val listOfValues = mutableListOf<Element>()

        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            when (matrix.idMatrix){
                1L ->{
                    Log.d("seahp_SelectYFragment", "set 1 alternativeList:")
                    alternativeList = Alternative().listByProject(project).toMutableList()
                    criteriaX = Criteria().getX(requireContext())
                    for (itemY in alternativeList){
                        val check = Element().toEvaluate(matrix, project, user, criteriaX.idCriteria, itemY.idAlternative)
                        listOfValues.add(check)
                        Log.d("seahp_SelectYFragment", "set 1 check")
                    }
                    Log.d("seahp_SelectYFragment", "set 1 listOfValues: $listOfValues")
                }
                2L ->{
                    Log.d("seahp_SelectYFragment", "set 2 criteriaList:")
                    criteriaList = Criteria().listByProject(project).toMutableList()
                    criteriaX = Criteria().getX(requireContext())
                    for (itemY in criteriaList){
                        val check = Element().toEvaluate(matrix, project, user, criteriaX.idCriteria, itemY.idCriteria)
                        listOfValues.add(check)
                        Log.d("seahp_SelectYFragment", "set 2 check")
                    }
                    Log.d("seahp_SelectYFragment", "set 2 listOfValues: $listOfValues")
                }
                else ->{
                    Log.d("seahp_SelectYFragment", "set 3 alternativeList:")
                    alternativeList = Alternative().listByProject(project).toMutableList()
                    alternativeX = Alternative().getX(requireContext())
                    for (itemY in alternativeList){
                        val check = Element().toEvaluate(matrix, project, user, alternativeX.idAlternative, itemY.idAlternative)
                        listOfValues.add(check)
                        Log.d("seahp_SelectYFragment", "set 3 check")
                    }
                    Log.d("seahp_SelectYFragment", "set 3 listOfValues: $listOfValues")
                }
            }
            withContext(Dispatchers.Main){
                //
                setRecyclerView(criteriaList, alternativeList, listOfValues)
                loadingDialog.dismiss()
            }
        }
    }

    private fun setRecyclerView(criteriaList: List<Criteria>, alternativeList: List<Alternative>,
                                listOfValues: List<Element>){
        //
        binding.selectAssessAlternativeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SelectYRecyclerViewAdapter(criteriaList, alternativeList, listOfValues)
        binding.selectAssessAlternativeRecyclerView.adapter = adapter
    }

    private fun buttonOperation(){
        //
        with(binding){
            //
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}