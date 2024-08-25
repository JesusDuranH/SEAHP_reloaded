package com.aem.sheap_reloaded.ui.project.create.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.code.things.TextConfig
import com.aem.sheap_reloaded.databinding.FragmentProjectCreateBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectCreateFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var next: Button
    private lateinit var config: Cipher
    private lateinit var configText: TextConfig
    private lateinit var configProject: SEAHP

    private var _binding: FragmentProjectCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val projectCreateViewModel =
            ViewModelProvider(this)[ProjectCreateViewModel::class.java]

        _binding = FragmentProjectCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProjectCreate
        projectCreateViewModel.text.observe(viewLifecycleOwner){
            textView.text = it
        }

        setVar()
        if (!getData()) return root
        buttonOperation()

        return root
    }

    private fun setVar(){
        //
        val context = requireContext()
        config = Cipher()
        configText = TextConfig(context)
        configProject = SEAHP()
    }

    private fun getData(): Boolean{
        //
        user = User().get(requireContext())
        if (user == User()) {
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            binding.projectCreateButtonNext.isEnabled = false
            binding.projectCreateTextinputEdittextName.isEnabled = false
            binding.projectCreateTextinputEdittextDescription.isEnabled = false
            return false
        }
        return true
    }

    private fun buttonOperation(){
        next = binding.projectCreateButtonNext
        next.setOnClickListener {
            getNameProject()
        }
        configProject.setStatus(false, requireContext())
    }

    private fun getNameProject(): Boolean{
        with(binding){
            with(configText){

                val name = getInfo(projectCreateTextinputEdittextName,
                    projectCreateTextinputLayoutName, 6)
                val desc = getInfo(projectCreateTextinputEdittextDescription,
                    projectCreateTextinputLayoutDescription, 7)

                if (name == null || desc == "-1") return false

                val loadingDialog = LoadingDialogFragment.newInstance("Creando Proyecto...")
                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    val newProject = withContext(Dispatchers.IO){
                        Project().create(name, desc)
                    }
                    val newParticipant = withContext(Dispatchers.IO){
                        Participant().create(user, newProject, 2)
                    }
                    Project().set(newProject, requireContext())
                    withContext(Dispatchers.Main) {
                        loadingDialog.dismiss()
                        clearText(projectCreateTextinputEdittextName)
                        clearText(projectCreateTextinputEdittextDescription)
                        findNavController().navigate(R.id.action_nav_project_create_to_create_criteria)
                    }
                }
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}