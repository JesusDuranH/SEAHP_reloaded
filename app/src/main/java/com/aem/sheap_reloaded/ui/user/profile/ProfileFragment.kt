package com.aem.sheap_reloaded.ui.user.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.databinding.FragmentUserProfileBinding

class ProfileFragment: Fragment() {
    //
    private lateinit var configProject: SEAHP

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //
        val profileView =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textProfile
        profileView.text.observe(viewLifecycleOwner){
            textView.text = it
        }

        setVar()

        return root
    }

    private fun setVar(){
        //
        configProject = SEAHP()
        val context = requireContext()
        val user: User = User().get(context)
        if (user != User()) setUser(user)
        else {
            binding.profileButtonLogout.isEnabled = false
            binding.profileButtonEdit.isEnabled = false
        }
    }

    private fun setUser(data: User){
        //
        with(binding){
            "${getString(R.string.user_user)}: ${data.user}".also { profileTextviewUser.text = it }
            "${getString(R.string.user_name)}: ${data.name}".also { profileTextviewName.text = it }
            "${getString(R.string.user_email)}: ${data.mail}".also { profileTextviewEmail.text = it }
            //Programar despues la edicion
            profileButtonEdit.isEnabled = false
            //
            profileButtonLogout.setOnClickListener {
                User().set(User(), requireContext())
                Log.d("seahp_ProfileFragment", "setUser: Logout")
                findNavController().navigate(R.id.action_nav_profile_to_home_logout)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/*

class ProfileFragment: Fragment() {



}
* */