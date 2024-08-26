package com.aem.sheap_reloaded.ui.project.create.criteria

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
import com.aem.sheap_reloaded.code.objects.Criteria
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.code.things.TextConfig
import com.aem.sheap_reloaded.databinding.FragmentProjectCreateCriteriaBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CriteriaFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var dataList: MutableList<Criteria>
    private lateinit var configText: TextConfig
    private lateinit var configProject: SEAHP

    private var _binding: FragmentProjectCreateCriteriaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val projectCreateCriteriaViewModel =
            ViewModelProvider(this)[ProjectCreateCriteriaViewModel::class.java]

        _binding = FragmentProjectCreateCriteriaBinding.inflate(inflater, container,false)
        val root: View = binding.root

        val textView: TextView = binding.textProjectCreateCriteria
        projectCreateCriteriaViewModel.text.observe(viewLifecycleOwner){
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
        project = Project().get(context)
        configText = TextConfig(context)
        configProject = SEAHP()
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
        return true
    }

    private fun disable(){
        //
        with(binding){
            //
            projectCreateCriteriaButtonAdd.isEnabled = false
            projectCreateCriteriaButtonNext.isEnabled = false
            projectCreateCriteriaTextinputEdittextName.isEnabled = false
            projectCreateCriteriaTextinputEdittextDesc.isEnabled = false
        }
    }

    private fun set(){
        //
        dataList = emptyList<Criteria>().toMutableList()
        setRecyclerView(dataList)

        val loadingDialog = LoadingDialogFragment.newInstance("Cargando Criterios...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            val list = Criteria().listByProject(project)
            for (item in list){
                dataList.add(item)
            }
            withContext(Dispatchers.Main){
                //
                setRecyclerView(dataList)
                loadingDialog.dismiss()
            }
        }
    }

    private fun setRecyclerView(dataList: List<Criteria>){
        binding.projectCreateCriteriaRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CriteriaRecyclerViewAdapter(dataList)
        binding.projectCreateCriteriaRecyclerView.adapter = adapter
    }

    private fun buttonOperation(){
        val isEdit = configProject.getStatus(requireContext())
        val next = binding.projectCreateCriteriaButtonNext
        next.setOnClickListener {
            if (isEdit) findNavController().navigate(R.id.action_nav_project_create_criteria_to_project_edit,
                null,
                NavOptions.Builder().setPopUpTo(R.id.nav_project_edit,true).build())
            else findNavController().navigate(R.id.action_nav_project_create_criteria_to_create_alternative)
        }
        val add = binding.projectCreateCriteriaButtonAdd
        add.setOnClickListener {
            getCriteriaData()
        }
    }

    private fun getCriteriaData(): Boolean{
        with(binding){
            with(configText){

                val name = getInfo(projectCreateCriteriaTextinputEdittextName,
                    projectCreateCriteriaTextinputLayoutName, 6)
                val desc = getInfo(projectCreateCriteriaTextinputEdittextDesc,
                    projectCreateCriteriaTextinputLayoutDesc, 7)
                if (name == null) return false

                val loadingDialog = LoadingDialogFragment.newInstance("Creando Criterio...")
                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    val newCriteria = Criteria().create(name, desc, null, project)
                    withContext(Dispatchers.Main){
                        //
                        dataList.add(newCriteria)
                        loadingDialog.dismiss()
                        clearText(projectCreateCriteriaTextinputEdittextName)
                        clearText(projectCreateCriteriaTextinputEdittextDesc)
                        setRecyclerView(dataList)
                    }
                }
                return true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}