package com.aem.sheap_reloaded.ui.project.create.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.FragmentProjectCreateParticipantBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParticipantFragment: Fragment(), OnChangeParticipantListener {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var userList: MutableList<User>
    private lateinit var configProject: SEAHP
    private lateinit var participantList: MutableList<Participant>

    private var _binding: FragmentProjectCreateParticipantBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val projectCreateUsersViewModel =
            ViewModelProvider(this)[ParticipantViewModel::class.java]

        _binding = FragmentProjectCreateParticipantBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProjectCreateUsers
        projectCreateUsersViewModel.text.observe(viewLifecycleOwner){
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
        config = Cipher()
        configProject = SEAHP()
        user = User().get(context)
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
        return true
    }

    private fun disable(){
        //
        with(binding){
            projectCreateUsersButtonNext.isEnabled = false
            projectCreateUsersButtonSearch.isEnabled = false
        }
    }

    private fun set(){
        //
        userList = emptyList<User>().toMutableList()
        participantList = emptyList<Participant>().toMutableList()
        val loadingDialog = LoadingDialogFragment.newInstance("Cargando Usuarios...")
        setRecyclerView(userList, participantList)
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            userList = User().listAll().toMutableList()
            participantList = Participant().listByProject(project).toMutableList()
            withContext(Dispatchers.Main){
                setRecyclerView(userList, participantList)
                loadingDialog.dismiss()
            }
        }
    }

    private fun setNewUser(ty: Int?, type: Int, user: User, project: Project, delete: Boolean){
        val loadingDialog = LoadingDialogFragment.newInstance("Actualizando Usuarios...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            if (delete) {
                if (ty != null){
                    Participant().delete(user, project)
                }
            } else {
                if (ty == null) {
                    Participant().create(user, project, type)
                } else {
                    Participant().update(user, project, type)
                }
            }
            withContext(Dispatchers.Main){
                setRecyclerView(userList, participantList)
                loadingDialog.dismiss()
                set()
            }
        }
    }

    private fun setRecyclerView(dataList: List<User>, participantList: List<Participant>){
        //
        binding.projectUsersRecyclerViewUserId.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ParticipantRecyclerViewAdapter(dataList, participantList, this)
        binding.projectUsersRecyclerViewUserId.adapter = adapter
    }

    private fun buttonOperation(){
        val isEdit = configProject.getStatus(requireContext())
        val next: Button = binding.projectCreateUsersButtonNext
        next.setOnClickListener {
            if (isEdit) findNavController().navigate(R.id.action_nav_project_create_users_to_project_edit,
                null,
                NavOptions.Builder().setPopUpTo(R.id.nav_project_edit,true).build())
            else findNavController().navigate(R.id.action_nav_project_create_users_to_list_finish,
                null,
                NavOptions.Builder().setPopUpTo(R.id.nav_project_list, true).build())
        }
        val search = binding.projectCreateUsersButtonSearch
        search.setOnClickListener {
            //programar busqueda de usuario
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDataChanged(ty: Int?, type: Int, user: User, project: Project, delete: Boolean) {
        setNewUser(ty, type, user, project, delete)
    }
}