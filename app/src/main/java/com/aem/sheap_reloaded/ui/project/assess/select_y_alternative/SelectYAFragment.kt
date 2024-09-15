package com.aem.sheap_reloaded.ui.project.assess.select_y_alternative

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
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
import com.aem.sheap_reloaded.databinding.FragmentAssessSelectYAlternativeBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectYAFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var matrix: Matrix
    private lateinit var project: Project
    private lateinit var elementsYList: MutableList<Alternative>

    private var _binding: FragmentAssessSelectYAlternativeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val assessCriteriaAlternativeViewModel =
            ViewModelProvider(this)[SelectYAViewModel::class.java]
        _binding = FragmentAssessSelectYAlternativeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val textView: TextView = binding.textAssessAlternative
        assessCriteriaAlternativeViewModel.text.observe(viewLifecycleOwner){
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
        binding.assessAlternativeButtonSave.isEnabled = false
    }

    private fun set(){
        //
        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {

            val criteriaX = Criteria().getX(requireContext())
            binding.assessCriteriaTextName.text = criteriaX.nameCriteria

            Log.d("seahp_SelectYAFragment", "set 1 alternativeList:")
            elementsYList = Alternative().listByProject(project).toMutableList()

            val listOfValues = Element().getAlternativeRow(matrix, project, user, criteriaX).toMutableList()
            Log.d("seahp_SelectYAFragment", "set 1 listOfValues: $listOfValues")

            withContext(Dispatchers.Main){
                //
                loadingDialog.dismiss()
                setRecyclerView(elementsYList, criteriaX, listOfValues)
                //buttonOperation(elementsXList, criteriaX, listOfValues)
            }
        }
    }

    private fun setRecyclerView(columnList: List<Alternative>, criteriaX: Criteria, allList: List<Element>){
        //
        binding.assessAlternativeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SelectYARecyclerVIewAdapter(columnList, criteriaX, allList)
        binding.assessAlternativeRecyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

/*
    private fun buttonOperation(columnList: List<Element>, elementA: Element, allList: List<Element>){
        with(binding){
            assessAlternativeButtonSave.setOnClickListener {
                //
                val elementList = mutableListOf<String>()

                for (i in 0 until AssessCriteriaRecyclerViewAdapter(columnList, elementA, allList, dbHelper).itemCount) {
                    val viewHolder = binding.assessCriteriaRecyclerView.findViewHolderForAdapterPosition(i)
                            as? AssessCriteriaRecyclerViewAdapter.AssessCriteriaHolder
                    Log.d("EditTextValue", "ViewHolder $i - $viewHolder ")
                    viewHolder?.let {
                        val editTextContent = it.binding.projectItemTextinputEdittextValue.text.toString()
                        val text = it.binding.projectItemTextinputLayoutValue.hint
                        elementList.add(editTextContent)
                        //Log.d("EditTextValue", "Item add $i - $text ")
                        //Log.d("EditTextValue", "Item value $editTextContent")
                    }
                    //Log.d("EditTextValue", "Item Size ${AssessCriteriaRecyclerViewAdapter(columnList, elementA, allList, dbHelper).itemCount}")
                }

                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    //
                    for ((index, value) in elementList.withIndex()) {

                        Log.d("EditTextValue", "Item $index: $value")
                        var scale = 0.0
                        if (value != "") scale = value.toDouble()
                        val mat = configProject.getMatrix(requireContext())
                        //Log.d("EditTextValue", "Matrix: $mat")
                        //Log.d("EditTextValue", "Matrix Project: ${mat.project}")

                        val itExist = allList.find { it.xElement == (index + 1) && it.yElement == elementA.yElement }
                        Log.d("EditTextValue", "Exist: $itExist")
                        if (itExist == null) {
                            //
                            if (scale != 0.0) {
                                val name = "Element ${elementA.nameElement} - ${columnList[index].nameElement}"
                                var newElement = Element()
                                if (isOnline){
                                    //
                                    val threadElement = Thread {
                                        //
                                        newElement = Element().createElementOnline((index + 1),
                                            elementA.yElement, name, null, scale, mat, project, user, 2)
                                    }.apply {
                                        start()
                                        join()
                                    }
                                } else newElement = Element().createElementOffline((index + 1),
                                    elementA.yElement, name, null, scale, mat, project, user, dbHelper, 2)

                                Log.d("EditTextValue", "New: $newElement")
                            }
                        } else {
                            //
                            if (itExist.scaleElement != scale){
                                //
                                var updateValue = Element()
                                if (isOnline){
                                    //
                                    val threadElement = Thread {
                                        //
                                        updateValue = Element().updateScaleElementOnline(itExist, scale, false)
                                    }.apply {
                                        start()
                                        join()
                                    }
                                } else updateValue = Element().updateScaleElementOffline(itExist, scale, dbHelper, false)

                                Log.d("EditTextValue", "Update: $updateValue")
                            }
                        }
                    }
                    withContext(Dispatchers.Main){
                        //
                        loadingDialog.dismiss()
                        findNavController().navigate(R.id.nav_project_select_assess,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.nav_project_select_assess, true).build())
                    }
                }
            }
        }
    }


}
* */