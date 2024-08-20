package com.aem.sheap_reloaded.ui.project.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.FragmentProjectListBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectListFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var config: Cipher
    private lateinit var adapter: ProjectListRecyclerViewAdapter
    private lateinit var configProject: SEAHP

    private var _binding: FragmentProjectListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstaceState: Bundle?
    ): View {

        val projectListViewModel =
            ViewModelProvider(this)[ProjectListViewModel::class.java]

        _binding = FragmentProjectListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProject
        projectListViewModel.text.observe(viewLifecycleOwner){
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
    }

    private fun getData(): Boolean{
        //
        if (user == User()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            binding.projectListButtonCreate.isEnabled = false
            return false
        }
        return true
    }

    private fun set(){
        //
        var participantList = emptyList<Participant>()
        val projectList = mutableListOf<Project>()
        setRecyclerView(participantList, projectList)

        val loadingDialog = LoadingDialogFragment.newInstance("Cargando Lista...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {

            participantList = Participant().byUsers(user)
            for (item in participantList){
                //val project = Project().getByID(item.project.idProject)
                projectList.add(item.project)
            }
            withContext(Dispatchers.Main){
                setRecyclerView(participantList, projectList)
                loadingDialog.dismiss()
            }

        }
    }

    private fun setRecyclerView(participantList: List<Participant>, projectList: List<Project>){
        binding.projectRecyclerViewName.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProjectListRecyclerViewAdapter(participantList, projectList)
        binding.projectRecyclerViewName.adapter = adapter
    }

    private fun buttonOperation(){
        val create: FloatingActionButton = binding.projectListButtonCreate
        create.setOnClickListener {
            //findNavController().navigate(R.id.action_nav_project_list_to_create)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}