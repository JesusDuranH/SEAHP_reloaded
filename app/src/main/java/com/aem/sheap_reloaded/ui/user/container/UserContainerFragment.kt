package com.aem.sheap_reloaded.ui.user.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.aem.sheap_reloaded.databinding.FragmentUserContainerBinding
import com.google.android.material.tabs.TabLayout

class UserContainerFragment: Fragment() {
    //
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private var _binding: FragmentUserContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserContainerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPager = binding.userContainerViewpager
        viewPager.adapter = UserPagerAdapter(requireContext(), childFragmentManager)

        tabLayout = binding.userContainerTablayout
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