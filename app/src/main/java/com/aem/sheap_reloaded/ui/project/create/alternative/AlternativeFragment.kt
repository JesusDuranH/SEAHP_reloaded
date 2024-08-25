package com.aem.sheap_reloaded.ui.project.create.alternative

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Alternative
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.code.things.TextConfig
import com.aem.sheap_reloaded.databinding.FragmentProjectCreateAlternativeBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlternativeFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var dataList: MutableList<Alternative>
    private lateinit var configText: TextConfig
    private lateinit var configProject: SEAHP

    private var _binding: FragmentProjectCreateAlternativeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val projectCreateAlternativeViewModel =
            ViewModelProvider(this)[ProjectCreateAlternativeViewModel::class.java]

        _binding = FragmentProjectCreateAlternativeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProjectCreateAlternative
        projectCreateAlternativeViewModel.text.observe(viewLifecycleOwner){
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
            projectCreateAlternativeButtonAdd.isEnabled = false
            projectCreateAlternativeButtonNext.isEnabled = false
            projectCreateAlternativeTextinputEdittextName.isEnabled = false
            projectCreateAlternativeTextinputEdittextDesc.isEnabled = false
        }
    }

    private fun set(){
        //
        //buttonOperation()
        dataList = emptyList<Alternative>().toMutableList()
        val loadingDialog = LoadingDialogFragment.newInstance("Cargando Alternativas...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            //
            val list = Alternative().listByProject(project)
            for (item in list){
                dataList.add(item)
            }
            withContext(Dispatchers.Main){
                setRecyclerView(dataList)
                loadingDialog.dismiss()
            }
        }
    }

    private fun setRecyclerView(dataList: List<Alternative>){
        binding.projectCreateAlternativeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AlternativeRecyclerViewAdapter(dataList)
        binding.projectCreateAlternativeRecyclerView.adapter = adapter
    }

    private fun buttonOperation(){
        val isEdit = configProject.getStatus(requireContext())
        val next: Button = binding.projectCreateAlternativeButtonNext
        next.setOnClickListener {
            if (isEdit) findNavController().navigate(
                R.id.action_nav_project_create_alternative_to_project_edit,
                null,
                NavOptions.Builder().setPopUpTo(R.id.nav_project_edit,true).build())
            //else findNavController().navigate(R.id.action_nav_project_create_alternative_to_create_users)
        }
        val add = binding.projectCreateAlternativeButtonAdd
        add.setOnClickListener {
            getAlternativeData()
        }
    }

    private fun getAlternativeData(): Boolean{
        with(binding){
            with(configText){
                val name = getInfo(projectCreateAlternativeTextinputEdittextName,
                    projectCreateAlternativeTextinputLayoutName, 6)
                val desc = getInfo(projectCreateAlternativeTextinputEdittextDesc,
                    projectCreateAlternativeTextinputLayoutDesc, 7)
                if (name == null) return false
                val loadingDialog = LoadingDialogFragment.newInstance("Creando Alternativas...")
                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    val newAlternative = Alternative().create(name, desc, project)
                    withContext(Dispatchers.Main){
                        //
                        dataList.add(newAlternative)
                        loadingDialog.dismiss()
                        clearText(projectCreateAlternativeTextinputEdittextName)
                        clearText(projectCreateAlternativeTextinputEdittextDesc)
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