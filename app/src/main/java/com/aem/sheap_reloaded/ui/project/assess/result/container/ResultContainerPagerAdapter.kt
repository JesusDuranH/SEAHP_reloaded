package com.aem.sheap_reloaded.ui.project.assess.result.container

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.ui.project.assess.result.alone.ResultFragment
import com.aem.sheap_reloaded.ui.project.assess.result.group.GroupResultFragment
import com.aem.sheap_reloaded.ui.project.assess.result.progress.ProgressFragment

private val TAB_TITTLE = arrayOf(
    R.string.menu_result_alone,
    R.string.menu_result_group,
    R.string.menu_result_participant
)

class ResultContainerPagerAdapter (private val context: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
    //
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> ResultFragment()
            1 -> GroupResultFragment()
            2 -> ProgressFragment()
            else -> throw IllegalArgumentException ("Invalid Position $position")
        }
    }
    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITTLE[position])
    }
    override fun getCount(): Int {
        return TAB_TITTLE.size
    }
}