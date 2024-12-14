package com.aem.sheap_reloaded.ui.project.assess.result.progress

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.Matrix
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.Result
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.FragmentAssessProgressBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgressFragment: Fragment() {
    //
    private lateinit var user: User
    private lateinit var matrix: Matrix
    private lateinit var config: Cipher
    private lateinit var project: Project
    private lateinit var configProject: SEAHP

    private var _binding: FragmentAssessProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProgressAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //
        val assessResultViewModel =
            ViewModelProvider(this)[ProgressViewModel::class.java]
        _binding = FragmentAssessProgressBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAssessResult
        assessResultViewModel.text.observe(viewLifecycleOwner){
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
        config = Cipher()
        matrix = Matrix().get(context)
        project = Project().get(context)
        configProject = SEAHP()
    }

    private fun getData(): Boolean{
        //
        if (user == User()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_user))
            return false
        }
        if (project == Project()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_project))
            return false
        }
        if (matrix == Matrix()){
            config.showMe(requireContext(), requireContext().getString(R.string.error_matrix))
            return false
        }
        return true
    }

    fun set(){
        //
        val loadingDialog = LoadingDialogFragment.newInstance("Cargando...")
        loadingDialog.show(childFragmentManager, "loadingDialog")
        CoroutineScope(Dispatchers.IO).launch {
            val group = Result().groupByProject(project)
            withContext(Dispatchers.Main){
                //
                Log.d("seahp_ProgressFragment", "group item: $group")
                recyclerView(group)
                loadingDialog.dismiss()
            }
        }
    }

    fun recyclerView(resultList: List<ResultGroup>){
        //
        binding.rvProgress.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProgressAdapter(resultList)
        binding.rvProgress.adapter = adapter
    }

}