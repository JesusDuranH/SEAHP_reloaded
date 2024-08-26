package com.aem.sheap_reloaded.ui.project.assess.result.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.aem.sheap_reloaded.code.objects.Participant
import com.aem.sheap_reloaded.code.objects.Project
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.databinding.FragmentAssessContainerBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ResultContainerFragment: Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private var _binding: FragmentAssessContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssessContainerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPager = binding.assessContainerViewpager
        viewPager.adapter = ResultContainerPagerAdapter(requireContext(), childFragmentManager)

        tabLayout = binding.assessContainerTablayout
        tabLayout.setupWithViewPager(viewPager)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setItem(item: Int){
        viewPager.currentItem = item
    }
}