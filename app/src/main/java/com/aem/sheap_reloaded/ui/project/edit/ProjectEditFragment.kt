package com.aem.sheap_reloaded.ui.project.edit

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
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.code.things.TextConfig
import com.aem.sheap_reloaded.databinding.FragmentProjectEditBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectEditFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var configText: TextConfig
    private lateinit var configProject: SEAHP

    private var _binding: FragmentProjectEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val projectEditViewModel =
            ViewModelProvider(this)[ProjectEditViewModel::class.java]

        _binding = FragmentProjectEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProjectEdit
        projectEditViewModel.text.observe(viewLifecycleOwner){
            textView.text = it
        }

        setVar()
        if (!getData()) return root
        buttonOperation(project)

        return root
    }

    private fun setVar(){
        //
        user = User().get(requireContext())
        config = Cipher()
        project = Project().get(requireContext())
        configText = TextConfig(requireContext())
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
            projectEditTextinputEdittextName.isEnabled = false
            projectEditTextinputEdittextDescription.isEnabled = false
            projectEditButtonCriteria.isEnabled = false
            projectEditButtonAlternative.isEnabled = false
            projectEditButtonUsers.isEnabled = false
            projectEditButtonSave.isEnabled = false
            projectEditButtonDelete.isEnabled = false
        }
    }

    private fun buttonOperation(data: Project){
        with(binding){
            projectEditTextinputEdittextName.setText(data.nameProject)
            projectEditTextinputEdittextDescription.setText(data.descriptionProject)
            projectEditButtonSave.setOnClickListener {
                with(configText){
                    val name = getInfo(projectEditTextinputEdittextName,
                        projectEditTextinputLayoutName, 6)
                    val desc = getInfo(projectEditTextinputEdittextDescription,
                        projectEditTextinputLayoutDescription, 7)
                    if (name != null) {
                        //
                        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
                        loadingDialog.show(childFragmentManager, "loadingDialog")
                        CoroutineScope(Dispatchers.IO).launch {
                            Project().update(data.idProject, name, desc)
                            withContext(Dispatchers.Main){
                                //
                                loadingDialog.dismiss()
                                findNavController().navigate(R.id.action_nav_project_edit_to_project_list,
                                    null,
                                    NavOptions.Builder().setPopUpTo(R.id.nav_project_list, true).build())

                            }
                        }
                    }
                }
            }
            projectEditButtonAlternative.setOnClickListener {
                //findNavController().navigate(R.id.action_nav_project_edit_to_project_alternative)
            }
            projectEditButtonCriteria.setOnClickListener {
                findNavController().navigate(R.id.action_nav_project_edit_to_project_criteria)
            }
            projectEditButtonUsers.setOnClickListener {
                //findNavController().navigate(R.id.action_nav_project_edit_to_project_users)
            }
            projectEditButtonDelete.isEnabled = false
            projectEditButtonDelete.setOnClickListener {
                //falta programar eliminar
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}