package com.aem.sheap_reloaded.ui.user.container

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.ui.user.login.LoginFragment
import com.aem.sheap_reloaded.ui.user.sign_up.SignUpFragment

private val TAB_TITTLE = arrayOf(
    R.string.menu_account_login,
    R.string.menu_account_sign_up
)

class UserPagerAdapter(private val context: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {
    //
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> LoginFragment()
            1 -> SignUpFragment()
            else -> throw IllegalArgumentException ("Invalid Position $position")
        }
    }
    override fun getPageTitle(position: Int): CharSequence {
        return context.resources.getString(TAB_TITTLE[position])
    }
    override fun getCount(): Int {
        return 2
    }
}