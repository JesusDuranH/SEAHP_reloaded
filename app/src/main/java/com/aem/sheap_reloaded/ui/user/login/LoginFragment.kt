package com.aem.sheap_reloaded.ui.user.login

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
import com.aem.sheap_reloaded.R
import com.aem.sheap_reloaded.code.objects.User
import com.aem.sheap_reloaded.code.things.Cipher
import com.aem.sheap_reloaded.code.things.SEAHP
import com.aem.sheap_reloaded.code.things.ShowPass
import com.aem.sheap_reloaded.code.things.TextConfig
import com.aem.sheap_reloaded.databinding.FragmentUserLoginBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment: Fragment() {
    //
    private lateinit var config: Cipher
    private lateinit var configText: TextConfig
    private lateinit var passButton: ShowPass
    private lateinit var loginButton: Button
    private lateinit var configProject: SEAHP

    private var _binding: FragmentUserLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loginViewModel =
            ViewModelProvider(this)[LoginViewModel::class.java]

        _binding = FragmentUserLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textLogin
        loginViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        setVar()
        buttonOperation()

        return root
    }

    private fun setVar(){
        //
        val context = requireContext()
        config = Cipher()
        configText = TextConfig(context)
        configProject = SEAHP()

    }

    private fun buttonOperation(){
        with(binding){
            loginButton = loginButtonEnter
            passButton = loginOnOffPass
            configText.viewPass(passButton, loginTextinputEdittextPass, requireContext(), view)
        }
        loginButton.setOnClickListener {
            getLoginData()
        }
    }

    private fun getLoginData(): Boolean{
        with(binding){
            with(configText){
                val user = getInfo(loginTextinputEdittextIdUser,
                    loginTextinputLayoutIdUser, 2)
                val onePass = getInfo(loginTextinputEdittextPass,
                    loginTextinputLayoutPass, 5)
                if (user == null || onePass == null) return false

                val loadingDialog = LoadingDialogFragment.newInstance("Login...")
                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    //
                    val select = withContext(Dispatchers.IO){
                        User().login(user, onePass, requireContext())
                    }
                    withContext(Dispatchers.Main){
                        //
                        loadingDialog.dismiss()
                        when (select){
                            1 ->{
                                showError(loginTextinputLayoutIdUser, R.string.error_user, requireContext())
                                clearText(loginTextinputEdittextPass)
                                Log.d("seahp_LoginFragment", "getLoginData select $select: Error Usuario")
                            }
                            2 -> {
                                showErrorNClean(loginTextinputLayoutPass,
                                    loginTextinputEdittextPass, R.string. error_other_pass, requireContext())
                                Log.d("seahp_LoginFragment", "getLoginData select $select: Error Contraseña")
                            }
                            3 ->{
                                clearText(loginTextinputEdittextIdUser)
                                clearText(loginTextinputEdittextPass)
                                config.showMe(requireContext(), requireContext().getString(R.string.login_true))
                                findNavController().navigate(R.id.action_nav_account_login_signup_to_home)
                                Log.d("seahp_LoginFragment", "getLoginData select $select: Success")
                            }
                            else -> {
                                Log.d("seahp_LoginFragment", "getLoginData select $select: Error")
                                config.showMe(requireContext(), "Error: $select")
                            }
                        }
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