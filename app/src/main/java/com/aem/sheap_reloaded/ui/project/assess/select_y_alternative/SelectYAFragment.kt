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

            Log.d("seahp_SelectYAFragment", "set 1 listOfValues:")
            val listOfValues = Element().getAlternativeColumn(matrix, project, user, criteriaX).toMutableList()

            withContext(Dispatchers.Main){
                //
                loadingDialog.dismiss()
                setRecyclerView(elementsYList, criteriaX, listOfValues)
                buttonOperation(elementsYList, criteriaX, listOfValues)
            }
        }
    }

    private fun setRecyclerView(columnList: List<Alternative>, criteriaX: Criteria, allList: List<Element>){
        //
        binding.assessAlternativeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SelectYARecyclerVIewAdapter(columnList, criteriaX, allList)
        binding.assessAlternativeRecyclerView.adapter = adapter
    }

    private fun buttonOperation(columnList: List<Alternative>, criteriaX: Criteria, allList: List<Element>){
        with(binding){
            assessAlternativeButtonSave.setOnClickListener {
                //
                val elementList = mutableListOf<Element>()

                for (i in 0 until SelectYARecyclerVIewAdapter(columnList, criteriaX, allList).itemCount) {
                    val viewHolder = binding.assessAlternativeRecyclerView.findViewHolderForAdapterPosition(i)
                            as? SelectYARecyclerVIewAdapter.AssessAlternativeHolder
                    Log.d("seahp_SelectYAFragment", "buttonOperation ViewHolder $i - $viewHolder ")
                    viewHolder?.let {
                        val scale = it.binding.projectItemTextinputEdittextValue.text.toString().toDouble()
                        val y = it.binding.projectItemTextAlternativeId.text.toString().toLong()
                        val newElement = Element(criteriaX.idCriteria, y, scale)
                        elementList.add(newElement)
                    }
                }
                Log.d("seahp_SelectYAFragment", "buttonOperation elementList $elementList ")

                val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    //
                    for (item in elementList){
                        var scale = 0.0
                        Log.d("seahp_SelectYAFragment", "buttonOperation Item $item")

                        if (item.scaleElement != null) scale = item.scaleElement
                        Log.d("seahp_SelectYAFragment", "buttonOperation scale $scale")
                        val alternativeY = Alternative().getByID(item.yElement, project)

                        val itExist = allList.find { it.xElement == criteriaX.idCriteria && it.yElement == alternativeY.idAlternative }
                        Log.d("seahp_SelectYAFragment", "buttonOperation Exist: $itExist")
                        if (itExist == null) {
                            //
                            if (scale != 0.0) {
                                val name = "Element ${criteriaX.nameCriteria} - ${alternativeY.nameAlternative}"
                                val threadElement = Thread {
                                    //
                                    val newElement = Element().create(criteriaX.idCriteria, alternativeY.idAlternative,
                                        name, null, scale, matrix, project, user, 2)
                                }.apply {
                                    start()
                                    join()
                                }
                            }
                        } else {
                            //
                            if (itExist.scaleElement != scale){
                                //
                                var updateValue = Element()
                                val threadElement = Thread {
                                    //
                                    updateValue = Element().updateScale(itExist, scale, false)
                                }.apply {
                                    start()
                                    join()
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main){
                        //
                        loadingDialog.dismiss()
                        findNavController().navigate(R.id.action_nav_project_assess_criteria_alternative_to_select_assess,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.nav_project_select_assess_x, true).build())
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}