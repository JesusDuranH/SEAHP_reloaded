package com.aem.sheap_reloaded.ui.project.create

class ProjectCreateFragment {
}

/*
package com.aem.seahp.ui.project_create

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
import androidx.navigation.fragment.findNavController
import com.aem.seahp.R
import com.aem.seahp.code.config.Config
import com.aem.seahp.code.dbFiles.SEAHPDBHelper
import com.aem.seahp.code.project.ConfigProject
import com.aem.seahp.code.text.ConfigText
import com.aem.seahp.code.types.Participant
import com.aem.seahp.code.types.Project
import com.aem.seahp.code.types.User
import com.aem.seahp.code.user.ConfigUser
import com.aem.seahp.databinding.FragmentProjectCreateBinding
import com.aem.seahp.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ProjectCreateFragment: Fragment() {
    private lateinit var user: User
    private lateinit var next: Button
    private lateinit var config: Config
    private lateinit var dbHelper: SEAHPDBHelper
    private lateinit var configText: ConfigText
    private lateinit var configUser: ConfigUser
    private lateinit var configProject: ConfigProject

    private lateinit var loadingDialog: DialogFragment

    private var _binding: FragmentProjectCreateBinding? = null
    private val binding get() = _binding!!
    private var isOnline = false

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
        config = Config()
        dbHelper = SEAHPDBHelper(context)
        configText = ConfigText(context)
        configUser = ConfigUser()
        configProject = ConfigProject()
        isOnline = configProject.getConnect(context)

        loadingDialog = LoadingDialogFragment()

        Log.d("Files", "Project Create Online: $isOnline")
    }

    private fun getData(): Boolean{
        //
        user = configUser.get(requireContext())
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
            //if (getNameProject())findNavController().navigate(R.id.action_nav_project_create_to_create_criteria)
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

                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    //
                    val newProject: Project
                    val newParticipant: Participant
                    if (isOnline) {
                        //
                        newProject = withContext(Dispatchers.IO){
                            Project().createProjectOnline(name, desc)
                        }
                        newParticipant = withContext(Dispatchers.IO){
                            Participant().createParticipantOnline(user, newProject, 2)
                        }
                        configProject.setProject(newProject, requireContext())
                    } else {
                        //
                        newProject = Project().createProjectOffline(name, desc, dbHelper)
                        newParticipant = Participant().createParticipantOffline(user, newProject, 2, dbHelper)
                        configProject.setProject(newProject, requireContext())
                    }
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
* */