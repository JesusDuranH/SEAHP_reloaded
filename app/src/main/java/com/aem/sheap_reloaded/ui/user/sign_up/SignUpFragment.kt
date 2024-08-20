package com.aem.sheap_reloaded.ui.user.sign_up

import android.content.Context
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
import com.aem.sheap_reloaded.databinding.FragmentUserSignupBinding
import com.aem.sheap_reloaded.ui.loading_dialog.LoadingDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpFragment: Fragment() {
    //
    private lateinit var opUser: User
    private lateinit var config: Cipher
    private lateinit var passButton: ShowPass
    private lateinit var configText: TextConfig
    private lateinit var passCButton: ShowPass
    private lateinit var signupButton: Button
    private lateinit var configProject: SEAHP

    private var _binding: FragmentUserSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val signUpViewModel =
            ViewModelProvider(this)[SignUpViewModel::class.java]

        _binding = FragmentUserSignupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSingUp
        signUpViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        setVar()
        buttonOperation(requireContext(), view)

        return root
    }

    private fun setVar(){
        //
        val context = requireContext()
        config = Cipher()
        configText = TextConfig(context)
        configProject = SEAHP()
    }

    private fun buttonOperation(context: Context, view: View?){
        with(binding){
            passButton = signUpOnOffPass
            passCButton = signUpOnOffPassConfirm
            signupButton = signUpButtonEnter
            with(configText) {
                viewPass(passButton, signUpTextinputEdittextPass, context, view)
                viewPass(passCButton, signUpTextinputEdittextPassConfirm, context, view)
            }
        }
        signupButton.setOnClickListener {
            getSignUpData()
        }
    }

    private fun getSignUpData(): Boolean{
        with(binding){
            with(configText){
                val user = getInfo(signUpTextinputEdittextUser,
                    signUpTextinputLayoutUser, 2)
                val name = getInfo(signUpTextinputEdittextName,
                    signUpTextinputLayoutName, 3)
                val mail = getInfo(signUpTextinputEdittextEmail,
                    signUpTextinputLayoutEmail, 4)
                val onePass = getInfo(signUpTextinputEdittextPass,
                    signUpTextinputLayoutPass, 5)
                val twoPass = getInfo(signUpTextinputEdittextPassConfirm,
                    signUpTextinputLayoutPassConfirm, 5)
                if (user == null || name == null || mail == null ||
                    onePass == null || twoPass == null) return false

                val loadingDialog = LoadingDialogFragment.newInstance("Registrando...")
                loadingDialog.show(childFragmentManager, "loadingDialog")
                CoroutineScope(Dispatchers.IO).launch {
                    //
                    val getUser = User().heExist(user)
                    val getMail = User().mailExist(mail)

                    if (getUser || getMail) {
                        //
                        withContext(Dispatchers.Main){
                            if (getUser) showError(signUpTextinputLayoutUser, R.string.error_user_oc, requireContext())
                            if (getMail) showError(signUpTextinputLayoutEmail, R.string.error_mail_oc, requireContext())
                            loadingDialog.dismiss()
                        }
                        return@launch
                    }

                    if (onePass == twoPass){
                        withContext(Dispatchers.Main){
                            opUser = User().register(user, name, mail, onePass)
                            Log.d("seahp_SignUpFragment", "getSignUpData: Register")
                            User().login(opUser.user, opUser.pass, requireContext())
                            Log.d("seahp_SignUpFragment", "getSignUpData: Login")
                            with(config){
                                clearText(signUpTextinputEdittextUser)
                                clearText(signUpTextinputEdittextName)
                                clearText(signUpTextinputEdittextEmail)
                                clearText(signUpTextinputEdittextPass)
                                clearText(signUpTextinputEdittextPassConfirm)
                            }
                            loadingDialog.dismiss()
                            findNavController().navigate(R.id.action_nav_account_login_signup_to_home)
                        }
                        return@launch
                    } else {
                        //
                        withContext(Dispatchers.Main){
                            showErrorNClean(
                                signUpTextinputLayoutPass, signUpTextinputEdittextPass,
                                R.string.error_between_pass, requireContext()
                            )
                            showErrorNClean(
                                signUpTextinputLayoutPassConfirm,
                                signUpTextinputEdittextPassConfirm, R.string.error_between_pass,
                                requireContext()
                            )
                            loadingDialog.dismiss()
                        }
                        return@launch
                    }
                }
                return true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}