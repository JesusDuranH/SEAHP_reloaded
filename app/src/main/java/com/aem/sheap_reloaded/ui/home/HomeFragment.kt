package com.aem.sheap_reloaded.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aem.sheap_reloaded.databinding.FragmentHomeBinding
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.AzureHelper
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var user: User
    private lateinit var project: Project
    private lateinit var configProject: SEAHP

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var login = false
    private var lastProject = false
    private var connection = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        getConnection()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getConnection(){
        val loading = LoadingDialogFragment.newInstance("Conectando...")
        loading.show(childFragmentManager, "loadingDialog") //Inicia Loading
        CoroutineScope(Dispatchers.IO).launch {
            do {
                connection = AzureHelper().getConnection()
                if (!connection) delay(1000)
            } while (!connection)

            withContext(Dispatchers.Main){
                loading.dismiss()                                 //Termina Loading
                setVar()
            }
        }
    }

    private fun setVar(){
        //
        val context = requireContext()
        configProject = SEAHP()

        val loading = LoadingDialogFragment.newInstance("Cargando Datos...")
        loading.show(childFragmentManager, "seahp_loadingDialog") //Inicia Loading
        CoroutineScope(Dispatchers.IO).launch {
            try {
                user = User().get(context)
                if (user != User()){
                    login = User().login(user.user, user.pass, context) == 3 //Consulta a DB
                    if (!login) user = User()                           //Procesar la Info
                }
                Log.d("seahp_HomeFragment", "setVar: login = $login")

            }
            catch (e: Exception){
                Log.d("seahp_HomeFragment", "setVar: login = null\n${e.message}")
                User()
            }
            try {
                project = Project().get(context)
                if (project != Project()){
                    val exist = Project().getByID(project.idProject)
                    if (exist != Project()) lastProject = true
                }
                Log.d("seahp_HomeFragment", "setVar: project = $project")
            }
            catch (e: Exception) {
                Log.d("seahp_HomeFragment", "setVar: project = null\n${e.message}")
                Project()
            }
            withContext(Dispatchers.Main){
                if (login) binding.textHomeUser.text = getString(R.string.home_user_welcome_1) + user.user // Resultado
                loading.dismiss() //Termina Loading
                buttonOperation()
            }
        }
    }

    private fun buttonOperation(){
        val profile: FloatingActionButton = binding.homeButtonProfile
        profile.setOnClickListener {
            if (login) findNavController().navigate(R.id.action_nav_home_to_profile)
            else findNavController().navigate(R.id.action_nav_home_to_login_sign_up)
        }

        val projectList: Button = binding.homeButtonProject
        if (!login) projectList.isEnabled = false
        projectList.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_project_list)
        }

        val lastProjectButton: Button = binding.homeButtonLastProject
        if (!lastProject || !login) {
            lastProjectButton.isEnabled = false
            lastProjectButton.text = getString(R.string.home_last_project_false)
        } else {
            val desc = if (project.descriptionProject == null) "" else project.descriptionProject
            lastProjectButton.text = "${project.nameProject}\n$desc"
        }
        lastProjectButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_matrix_last_project)
        }
    }
}